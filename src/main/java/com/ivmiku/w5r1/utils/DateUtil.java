package com.ivmiku.w5r1.utils;

import jakarta.annotation.PostConstruct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Aurora
 */
public class DateUtil {
    /**
     * 获取当前时间
     * @return 当前时间字符串
     */
    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ft.format(date);
    }

    /**
     * 转换为时间戳
     * @param time 时间字符串
     * @return 时间戳
     * @throws ParseException
     */
    public static long toTimeSig(String time) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = ft.parse(time);
        return date.getTime();
    }

    /**
     * 设置当前时区GMT+8
     */
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
