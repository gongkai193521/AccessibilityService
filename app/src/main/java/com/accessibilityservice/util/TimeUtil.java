package com.accessibilityservice.util;

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
}
