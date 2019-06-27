package com.xp.cache.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * 时间日期工具类
 */
public class DateUtils {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date StringToDate(String dateStr) {
        try {
            Date date=SIMPLE_DATE_FORMAT.parse(dateStr);
            return date;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
