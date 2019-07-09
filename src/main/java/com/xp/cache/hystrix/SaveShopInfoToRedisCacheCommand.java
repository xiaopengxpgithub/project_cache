package com.xp.cache.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.xp.cache.config.SpringContext;
import com.xp.cache.pojo.ShopInfo;
import com.xp.cache.utils.Constant;
import redis.clients.jedis.JedisCluster;

/**
 * 将店铺信息保存到redis缓存中
 */
public class SaveShopInfoToRedisCacheCommand extends HystrixCommand<Boolean> {

    private ShopInfo shopInfo;

    public SaveShopInfoToRedisCacheCommand(ShopInfo shopInfo){
        //使用同一个线程池中的线程处理
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
        );
        this.shopInfo=shopInfo;
    }

    @Override
    protected Boolean run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");

        String key = Constant.REDIS_CACHE_SHOPINFO + shopInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
        return true;
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }
}
