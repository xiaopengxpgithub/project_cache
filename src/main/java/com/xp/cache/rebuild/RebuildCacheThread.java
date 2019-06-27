package com.xp.cache.rebuild;

import com.xp.cache.config.SpringContext;
import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.service.ICacheService;
import com.xp.cache.utils.DateUtils;
import com.xp.cache.zookeeper.ZookeeperSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 缓存重建处理线程
 */
public class RebuildCacheThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RebuildCacheThread.class);

    @Override
    public void run() {
        //从缓存重建队列中取出数据
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZookeeperSession zookeeperSession = ZookeeperSession.getInstance();
        ICacheService iCacheService = SpringContext.getApplicationContext().getBean(ICacheService.class);

        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

            if(!StringUtils.isEmpty(productInfo)){
                //分布式锁
                zookeeperSession.acquireDistributedLock(productInfo.getId());
                //获取redis缓存中已经存在的数据
                ProductInfo existProductInfo = iCacheService.getProductInfoFromRedisCache(productInfo.getId());

                if (!StringUtils.isEmpty(existProductInfo)) {
                    Date exist_date = DateUtils.StringToDate(existProductInfo.getUpdateTime());
                    Date new_date = DateUtils.StringToDate(productInfo.getUpdateTime());

                    if (new_date.before(exist_date)) {
                        //最新数据的更新时间早于缓存数据的更新时间,不更新
                        LOGGER.info(productInfo.getId() + "对应的缓存数据已经被更新过了!");

                        //执行下一次循环.继续执行队列中下一条消息
                        continue;
                    }
                }

                //如果最新数据的更新时间晚于已经存在的缓存中的数据的时间/或者缓存中没有该数据,正常更新缓存中的数据
                //③.将查询到的商品信息缓存到redis缓存中
                LOGGER.info("更新" + productInfo.getId() + "数据缓存数据");
                iCacheService.saveProductInfoToRedis(productInfo);

                //更新redis缓存之后,释放zookeeper分布式锁
                zookeeperSession.releaseDistributedLock(productInfo.getId());
            }
        }
    }
}
