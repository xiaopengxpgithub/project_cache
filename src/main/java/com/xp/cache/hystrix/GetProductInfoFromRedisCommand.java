package com.xp.cache.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.xp.cache.config.SpringContext;
import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.utils.Constant;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

/**
 * 从redis缓存中获取数据
 */
public class GetProductInfoFromRedisCommand extends HystrixCommand<ProductInfo> {

    private Integer productId;

    public GetProductInfoFromRedisCommand(Integer productId){
        //使用同一个线程池中的线程处理
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
        );
        this.productId=productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");

        String key = Constant.REDIS_CACHE_PRODUCTINFO + productId;
        String result = jedisCluster.get(key);
        if (!StringUtils.isEmpty(result)) {
            return JSONObject.parseObject(result, ProductInfo.class);
        }
        return null;
    }

    @Override
    protected ProductInfo getFallback() {
        return null;
    }
}
