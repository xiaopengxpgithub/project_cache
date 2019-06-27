package com.xp.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.pojo.ShopInfo;
import com.xp.cache.rebuild.RebuildCacheQueue;
import com.xp.cache.service.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private ICacheService iCacheService;

    @RequestMapping(value = "/testPutCache")
    public String testPutCache() {

        try {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(1);
            productInfo.setName("iPhone手机");
            productInfo.setPrice(4999.00);

            iCacheService.saveLocalCache(productInfo);

            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "fail";
    }

    @RequestMapping(value = "/testGetCache")
    public ProductInfo testGetCache(Integer id) {
        try {

            ProductInfo productInfo = iCacheService.getLocalCache(id);

            return productInfo;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/getProductInfo")
    @ResponseBody
    public ProductInfo getProductInfo(Integer productId) {
        ProductInfo productInfo = null;

        productInfo = iCacheService.getProductInfoFromRedisCache(productId);
        LOGGER.info("=================从redis中获取缓存，商品信息=" + productInfo);

        if(productInfo == null) {
            productInfo = iCacheService.getProductInfoFromLocalCache(productId);
            LOGGER.info("=================从ehcache中获取缓存，商品信息=" + productInfo);
        }

        //如果本地缓存和redis缓存都拿不到数据,则需要进行缓存重建
        if(productInfo == null) {
            // 调用远程的productinfo服务,查询数据
            String productInfoJSON = "{\"id\": 2, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modified_time\": \"2017-01-01 12:01:00\"}";
            productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

            // 将数据推送到一个内存队列中
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
            rebuildCacheQueue.putProductInfo(productInfo);
        }

        return productInfo;
    }

    @RequestMapping("/getShopInfo")
    @ResponseBody
    public ShopInfo getShopInfo(Integer shopId) {
        ShopInfo shopInfo = null;

        shopInfo = iCacheService.getShopInfoFromRedisCache(shopId);
        System.out.println("=================从redis中获取缓存，店铺信息=" + shopInfo);

        if(shopInfo == null) {
            shopInfo = iCacheService.getShopInfoFromLocalCache(shopId);
            System.out.println("=================从ehcache中获取缓存，店铺信息=" + shopInfo);
        }

        if(shopInfo == null) {
            // 就需要从数据源重新拉去数据，重建缓存
            //参考上面的代码
        }

        return shopInfo;
    }

}
