package com.xp.cache.service;

import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.pojo.ShopInfo;

/**
 * 缓存service接口
 */
public interface ICacheService {

    public ProductInfo saveLocalCache(ProductInfo productInfo);

    public ProductInfo getLocalCache(Integer id);

    public ProductInfo saveProductInfoToLocal(ProductInfo productInfo);

    public ShopInfo saveShopInfoToLocal(ShopInfo shopInfo);

    public void saveProductInfoToRedis(ProductInfo productInfo);

    public void saveShopInfoToRedis(ShopInfo shopInfo);

    public ProductInfo getProductInfoFromLocalCache(Integer productId);

    public ShopInfo getShopInfoFromLocalCache(Integer shopId);

    public ProductInfo getProductInfoFromRedisCache(Integer productId);

    public ShopInfo getShopInfoFromRedisCache(Integer shopId);

}
