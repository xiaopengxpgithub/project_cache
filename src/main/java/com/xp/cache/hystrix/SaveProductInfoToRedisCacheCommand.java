package com.xp.cache.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.xp.cache.config.SpringContext;
import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.utils.Constant;
import redis.clients.jedis.JedisCluster;

/**
 * 将商品数据保存到redis缓存中的command
 */
public class SaveProductInfoToRedisCacheCommand extends HystrixCommand<Boolean> {

    private ProductInfo productInfo;

    public SaveProductInfoToRedisCacheCommand(ProductInfo productInfo) {
        //使用同一个线程池中的线程处理
        super(HystrixCommandGroupKey.Factory.asKey("RedisGroup"));
        this.productInfo = productInfo;
    }

    //执行业务逻辑--保存商品信息到redis缓存
    @Override
    protected Boolean run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");
        String key = Constant.REDIS_CACHE_PRODUCTINFO + productInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(productInfo));

        return true;
    }
}
