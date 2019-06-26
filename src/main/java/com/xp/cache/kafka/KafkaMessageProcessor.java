package com.xp.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.xp.cache.config.SpringContext;
import com.xp.cache.pojo.ProductInfo;
import com.xp.cache.pojo.ShopInfo;
import com.xp.cache.service.ICacheService;
import com.xp.cache.utils.Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Iterator;

/**
 * kafka消息处理线程
 */
public class KafkaMessageProcessor implements Runnable {

    private ConsumerRecords<String, String> consumerRecords;

    //注意:这个不能使用autowire注解
    private ICacheService iCacheService;

    public KafkaMessageProcessor(ConsumerRecords<String, String> consumerRecords) {
        this.consumerRecords = consumerRecords;
        //从spring容器中获取到iCacheService bean对象
        this.iCacheService = SpringContext.getApplicationContext().getBean(ICacheService.class);
    }

    @Override
    public void run() {
        Iterator<ConsumerRecord<String, String>> consumerRecordIterator = this.consumerRecords.iterator();
        while (consumerRecordIterator.hasNext()) {
            String message = new String(consumerRecordIterator.next().value());

            System.out.println("接受到的消息:" + message);

            //将接受到的商品信息变更的消息转换成json字符串
            JSONObject jsonObject = JSONObject.parseObject(message);

            //从json字符串中取出对应的服务的标识
            String serviceId = jsonObject.getString("serviceId");

            //调用对应的商品服务
            if (Constant.PRODUCTINFO_SERVICE.equals(serviceId)) {
                //如果当前是商品信息变更服务
                this.processProductIdInfoChangeMessage(jsonObject);
            } else if (Constant.SHOPINFO_SERVICE.equals(serviceId)) {
                //如果当前是店铺信息变更服务
                this.processShowInfoChangeMessage(jsonObject);
            }
        }
    }

    private void processShowInfoChangeMessage(JSONObject jsonObject) {
        //提取店铺ID
        Integer shopId = jsonObject.getInteger("shop_id");

        //①.调用远程店铺信息服务,获取最新的店铺信息
        //http://xxxx:8081/shopInfo/getShopInfo?shopId=shopId
        //假设调用远程店铺信息服务得到商品信息
        String shopInfoJSON = "{\"id\": 1, \"shopName\": \"小王的手机店\", \"shopGrade\": 5, \"shopFavorableRate\":0.99}";

        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
        //②.将查询到的店铺信息缓存到本地的ehcache缓存中
        iCacheService.saveShopInfoToLocal(shopInfo);

        System.out.println("====================获取缓存到本地的店铺信息:" + iCacheService.getShopInfoFromLocalCache(shopId));

        //③.将查询到的店铺信息缓存到redis缓存中
        iCacheService.saveShopInfoToRedis(shopInfo);

    }

    //处理商品信息变更的消息
    private void processProductIdInfoChangeMessage(JSONObject jsonObject) {
        //提取商品ID
        Integer productId = jsonObject.getInteger("product_id");

        //①.调用远程商品信息服务,获取最新的商品信息
        //http://xxxx:8081/productInfo/getProductInfo?productId=productId
        //假设调用远程商品信息服务得到商品信息
        String productInfoJSON = "{\"id\": 1, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"afterSaleService\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1}";

        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        //②.将查询到的商品信息缓存到本地的ehcache缓存中
        iCacheService.saveProductInfoToLocal(productInfo);
        System.out.println("====================获取缓存到本地的商品信息:" + iCacheService.getProductInfoFromLocalCache(productId));

        //③.将查询到的商品信息缓存到redis缓存中
        iCacheService.saveProductInfoToRedis(productInfo);
    }
}
