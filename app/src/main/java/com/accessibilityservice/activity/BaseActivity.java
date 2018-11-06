package com.accessibilityservice.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.InflateException;
import android.view.View;


/**
 * Created by gongkai on 2018/11/6.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public Activity mContext;

    /**
     * 填充布局文件,格式 return R.layout.activity_main
     */
    public abstract int setContentView();

    /**
     * 获取控件填充数据
     */
    public abstract void setupViews(Bundle savedInstanceState);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(setContentView());
        setupViews(savedInstanceState);
    }

    public <T extends View> T v(int id) {
        View view = this.findViewById(id);
        if (view == null) {
            throw new InflateException();
        }
        return (T) view;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
