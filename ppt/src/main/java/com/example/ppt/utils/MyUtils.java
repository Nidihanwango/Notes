package com.example.ppt.utils;

import java.util.Calendar;
import java.util.Date;

public class MyUtils {
    public static String getDayOfWeek(){
        String[] week = {"周日","周一","周二","周三","周四","周五","周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (i < 0){
            i = 0;
        }
        return week[i];
    }
}
