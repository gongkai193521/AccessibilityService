package com.accessibilityservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.accessibilityservice.manager.TaskManager;

/**
 * Created by gongkai on 2018/12/22.
 */

public class VolumeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            Log.i("----", "收到音量改变广播");
            TaskManager.getInstance().stop();
        }
    }
}