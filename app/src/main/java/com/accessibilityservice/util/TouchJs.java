package com.accessibilityservice.util;

import android.view.WindowManager;

import com.accessibilityservice.MainApplication;

import java.util.HashMap;
import java.util.Map;

public class TouchJs {
    public static int SWIPE_DOWN = 1;
    public static int SWIPE_LEFT = 2;
    public static int SWIPE_RIGHT = 3;
    public static int SWIPE_UP = 0;

    public boolean simpleSwipe(int i, int i2, boolean z) {
        int i3;
        int i4;
        WindowManager windowManager = (WindowManager) MainApplication.getContext().getSystemService("window");
        int width = (int) ((((double) windowManager.getDefaultDisplay().getWidth()) * 0.5d) / 2.0d);
        int height = (int) ((((double) windowManager.getDefaultDisplay().getHeight()) * 0.5d) / 2.0d);
        if (i == SWIPE_UP) {
            i3 = height - i2;
            i4 = width;
        } else if (i == SWIPE_DOWN) {
            i3 = height + i2;
            i4 = width;
        } else if (i == SWIPE_LEFT) {
            i4 = width - i2;
            i3 = height;
        } else if (i == SWIPE_RIGHT) {
            i4 = width + i2;
            i3 = height;
        } else {
            i3 = height;
            i4 = width;
        }
        if (z) {
            int random = (int) (Math.random() * 100.0d);
            width += random;
            height += random;
            i4 += random;
            i3 += random;
        }
        return swipe(width, height, i4, i3);
    }

    public boolean simpleSwipeDown(int i, boolean z) {
        return simpleSwipe(SWIPE_DOWN, i, z);
    }

    public boolean simpleSwipeLeft(int i, boolean z) {
        return simpleSwipe(SWIPE_LEFT, i, z);
    }

    public boolean simpleSwipeRight(int i, boolean z) {
        return simpleSwipe(SWIPE_RIGHT, i, z);
    }

    public boolean simpleSwipeUp(int i, boolean z) {
        return simpleSwipe(SWIPE_UP, i, z);
    }

    public boolean swipe(int i, int i2, int i3, int i4) {
        int i5;
        long j = (long) 15;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            Thread.sleep(j);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (i != i3) {
            if (i < i3) {
                i5 = i + 10;
                if (i5 >= i3) {
                    i5 = i3;
                }
            } else {
                i5 = i - 10;
                if (i5 <= i3) {
                    i5 = i3;
                }
            }
            try {
                Thread.sleep(j);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = i5;
        }
        while (i2 != i4) {
            if (i2 < i4) {
                i5 = i2 + 10;
                if (i5 >= i4) {
                    i5 = i4;
                }
            } else {
                i5 = i2 - 10;
                if (i5 <= i4) {
                    i5 = i4;
                }
            }
            try {
                Thread.sleep(j);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i2 = i5;
        }
        try {
            Thread.sleep(j);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}
