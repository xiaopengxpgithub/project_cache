package com.xp.cache.service.impl;

import com.xp.cache.hystrix.GetProductInfoFromRedisCommand;
import com.xp.cache.hystrix.GetShopInfoFromRedisCommand;
import com.xp.cache.hystrix.SaveProductInfoToRedisCacheCommand;
import com.xp.cache.hystrix.SaveShopInfoToRedisCacheCommand;
import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.pojo.ShopInfo;
import com.xp.cache.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class CacheServiceImpl implements ICacheService {

    @Autowired
    private JedisCluster jedisCluster;

    //指定要使用的ehcache缓存策略
    public static final String CACHE_NAME = "local";

    //@CachePut注解表示将对象存入到缓存中,key就是对象的主键
    @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
    @Override
    public ProductInfo saveLocalCache(ProductInfo productInfo) {
        //返回存入缓存的对象
        return productInfo;
    }

    //@Cacheable注解表示从缓存中查询缓存,key就是对象的主键
    @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
    @Override
    public ProductInfo getLocalCache(Integer id) {
        //如果从缓存到拿到了则返回缓存数据,否则返回null
        return null;
    }

    @CachePut(value = CACHE_NAME, key = "'product_info_'+#productInfo.getId()")
    @Override
    public ProductInfo saveProductInfoToLocal(ProductInfo productInfo) {
        return productInfo;
    }

    @CachePut(value = CACHE_NAME, key = "'shop_info_'+#shopInfo.getId()")
    @Override
    public ShopInfo saveShopInfoToLocal(ShopInfo shopInfo) {
        return shopInfo;
    }

    @Override
    public void saveProductInfoToRedis(ProductInfo productInfo) {
        //资源隔离
        SaveProductInfoToRedisCacheCommand saveProductInfoToRedisCacheCommand = new SaveProductInfoToRedisCacheCommand(productInfo);
        saveProductInfoToRedisCacheCommand.execute();
    }

    @Override
    public void saveShopInfoToRedis(ShopInfo shopInfo) {
        //资源隔离
        SaveShopInfoToRedisCacheCommand saveShopInfoToRedisCacheCommand = new SaveShopInfoToRedisCacheCommand(shopInfo);
        saveShopInfoToRedisCacheCommand.execute();
    }

    @Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
    @Override
    public ProductInfo getProductInfoFromLocalCache(Integer productId) {
        return null;
    }

    @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
    @Override
    public ShopInfo getShopInfoFromLocalCache(Integer shopId) {
        return null;
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Integer productId) {
        //资源隔离
        GetProductInfoFromRedisCommand getProductInfoFromRedisCommand = new GetProductInfoFromRedisCommand(productId);
        return getProductInfoFromRedisCommand.execute();
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Integer shopId) {
        //资源隔离
        GetShopInfoFromRedisCommand getShopInfoFromRedisCommand = new GetShopInfoFromRedisCommand(shopId);
        return getShopInfoFromRedisCommand.execute();
    }
}
