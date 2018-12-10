package com.accessibilityservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by panjichang on 18/11/12.
 */

public class TimeUtil {
    public static String fomartTime(Long time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }


    public static Long parseTime(String time){
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        try {
            return sdf.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
