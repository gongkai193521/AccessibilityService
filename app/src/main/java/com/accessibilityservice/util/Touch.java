package com.accessibilityservice.util;

import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Touch {
    private long mDownTime;
    private boolean mIsDown;
    private Method sInjectInputEventMethod;
    private InputManager sInputManager;

    public Touch() {
        try {
            this.sInputManager = (InputManager) InputManager.class.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Class cls = InputManager.class;
            String str = "injectInputEvent";
            Class[] clsArr = new Class[2];
            clsArr[0] = InputEvent.class;
            clsArr[1] = Integer.TYPE;
            this.sInjectInputEventMethod = cls.getMethod(str, clsArr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
    }

    private void injectMotionEvent(int i, int i2, long j, long j2, float f, float f2, float f3) {
        MotionEvent obtain = MotionEvent.obtain(j, j2, i2, f, f2, f3, 1.0f, 0, 1.0f, 1.0f, 0, 0);
        obtain.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        try {
            this.sInjectInputEventMethod.invoke(this.sInputManager, new Object[]{obtain, Integer.valueOf(0)});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void end(int i, int i2) {
        end(i, i2, 0);
    }

    public void end(int i, int i2, long j) {
        if (this.mIsDown) {
            this.mIsDown = false;
            if (0 == j) {
                j = SystemClock.uptimeMillis() - this.mDownTime;
            }
            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_UP, this.mDownTime, this.mDownTime + j, (float) i, (float) i2, 1.0f);
        }
    }

    public void move(int i, int i2, long j) {
        if (this.mIsDown) {
            if (0 == j) {
                j = SystemClock.uptimeMillis() - this.mDownTime;
            }
            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_MOVE, this.mDownTime, this.mDownTime + j, (float) i, (float) i2, 1.0f);
        }
    }

    public void scroll(int i, int i2, int i3, int i4) {
        long uptimeMillis = SystemClock.uptimeMillis();
        PointerProperties[] pointerPropertiesArr = new PointerProperties[]{new PointerProperties()};
        pointerPropertiesArr[0].clear();
        pointerPropertiesArr[0].id = 0;
        PointerCoords[] pointerCoordsArr = new PointerCoords[]{new PointerCoords()};
        pointerCoordsArr[0].clear();
        pointerCoordsArr[0].x = (float) i;
        pointerCoordsArr[0].y = (float) i2;
        pointerCoordsArr[0].pressure = 1.0f;
        pointerCoordsArr[0].size = 1.0f;
        pointerCoordsArr[0].setAxisValue(10, (float) i3);
        pointerCoordsArr[0].setAxisValue(9, (float) i4);
        try {
            this.sInjectInputEventMethod.invoke(this.sInputManager, new Object[]{MotionEvent.obtain(uptimeMillis, uptimeMillis, MotionEvent.ACTION_SCROLL, 1, pointerPropertiesArr, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0), Integer.valueOf(0)});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void start(int i, int i2) {
        if (!this.mIsDown) {
            this.mIsDown = true;
            this.mDownTime = SystemClock.uptimeMillis();
            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_DOWN, this.mDownTime, this.mDownTime, (float) i, (float) i2, 1.0f);
        }
    }
}
