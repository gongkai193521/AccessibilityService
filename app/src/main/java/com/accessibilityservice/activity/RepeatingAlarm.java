package com.accessibilityservice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author panjichang (pjc916568516@@163.com)
 * @date 18/12/8
 * Copyright © 2018. All rights reserved.
 */

public class RepeatingAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ScriptListActivity activity = ScriptListActivity.getActivity();
        if (activity!=null){
            activity.onRandomRuns(null);
        }
    }
}
