package com.xp.cache.pojo;

import java.io.Serializable;

/**
 * 店铺信息
 */
public class ShopInfo implements Serializable {

    private Integer id;
    private String shopName;
    private int shopGrade;
    private double shopFavorableRate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getShopGrade() {
        return shopGrade;
    }

    public void setShopGrade(int shopGrade) {
        this.shopGrade = shopGrade;
    }

    public double getShopFavorableRate() {
        return shopFavorableRate;
    }

    public void setShopFavorableRate(double shopFavorableRate) {
        this.shopFavorableRate = shopFavorableRate;
    }

    public ShopInfo() {
    }

    @Override
    public String toString() {
        return "ShopInfo{" +
                "id=" + id +
                ", shopName='" + shopName + '\'' +
                ", shopGrade=" + shopGrade +
                ", shopFavorableRate=" + shopFavorableRate +
                '}';
    }
}
