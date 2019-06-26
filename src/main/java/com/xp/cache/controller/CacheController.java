package com.xp.cache.controller;

import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

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
}
