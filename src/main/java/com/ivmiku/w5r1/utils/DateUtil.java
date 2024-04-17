package com.ivmiku.w5r1.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getCurrentTime() {
        Date date = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return ft.format(date);
    }

    public static long toTimeSig(String time) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = ft.parse(time);
        return date.getTime();
    }
}
