package com.accessibilityservice.util;

import android.hardware.input.InputManager;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by gackor on 16/9/26.
 */
public class EventUtil {
    public static void injectMotionEvent(long downTime, int action, float x, float y) {
        InputManager im = null;
        Method event = null;
        try {
            im = (InputManager) InputManager.class.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            MotionEvent.class.getDeclaredMethod("obtain", new Class[0]).setAccessible(true);
            event = InputManager.class.getMethod("injectInputEvent", new Class[]{InputEvent.class, Integer.TYPE});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        MotionEvent localMotionEvent = MotionEvent.obtain(downTime, downTime, action, x, y, 0);
        localMotionEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        try {
            event.invoke(im, new Object[]{localMotionEvent, Integer.valueOf(0)});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    public static String execCommand(String command) {
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec(command);
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + command);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line+" ");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            System.err.println(e);
        }finally{
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
        return "";
    }

}
