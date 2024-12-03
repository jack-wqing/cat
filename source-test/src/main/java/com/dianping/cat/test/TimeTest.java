package com.dianping.cat.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author:liuwenqing
 * @Date:2024/11/29 17:59
 * @Description:
 **/
public class TimeTest {

    public static void main(String[] args) throws ParseException {
        String str = "2024-11-11 12:00:00";
        int len = str.length();
        String date = str.substring(0, 10);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Long baseline = format.parse(date).getTime();

        long time = baseline.longValue();
        long metric = 1;
        boolean millisecond = true;

        for (int i = len - 1; i > 10; i--) {
            char ch = str.charAt(i);

            if (ch >= '0' && ch <= '9') {
                time += (ch - '0') * metric;
                metric *= 10;
            } else if (millisecond) {
                millisecond = false;
            } else {
                metric = metric / 100 * 60;
            }
        }
        System.out.println(time);
        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(parse.format(new Date(time)));
    }

}
