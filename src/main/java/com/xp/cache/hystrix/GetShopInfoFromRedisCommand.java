package com.xp.cache.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.xp.cache.config.SpringContext;
import com.xp.cache.pojo.ShopInfo;
import com.xp.cache.utils.Constant;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

/**
 * 从redis缓存中获取店铺信息
 */
public class GetShopInfoFromRedisCommand extends HystrixCommand<ShopInfo> {

    private Integer shopId;

    public GetShopInfoFromRedisCommand(Integer shopId) {
        //使用同一个线程池中的线程处理
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.shopId = shopId;
    }

    @Override
    protected ShopInfo run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");

        String key = Constant.REDIS_CACHE_SHOPINFO + shopId;
        String result = jedisCluster.get(key);
        if (!StringUtils.isEmpty(result)) {
            return JSONObject.parseObject(result, ShopInfo.class);
        }

        return null;
    }
}
