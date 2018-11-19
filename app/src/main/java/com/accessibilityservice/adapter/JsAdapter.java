package com.accessibilityservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.R;
import com.accessibilityservice.manager.TaskManager;
import com.accessibilityservice.model.AppModel;
import com.accessibilityservice.util.AppUtils;
import com.bumptech.glide.Glide;

import es.dmoral.toasty.Toasty;

public class JsAdapter extends BaseListAdapter<AppModel> {
    public JsAdapter(Context context) {
        super(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JsHolder mJsHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.js_item, null);
            mJsHolder = new JsHolder();
            mJsHolder.tv_app = convertView.findViewById(R.id.tv_name);
            mJsHolder.iv_app = convertView.findViewById(R.id.iv_icon);
            mJsHolder.btn_run = convertView.findViewById(R.id.btn_run);
            mJsHolder.btn_stop = convertView.findViewById(R.id.btn_stop);
            mJsHolder.tv_status = convertView.findViewById(R.id.tv_status);
            convertView.setTag(mJsHolder);
        } else {
            mJsHolder = (JsHolder) convertView.getTag();
        }
        final AppModel models = mList.get(position);
        if (models.isChoose) {
            mJsHolder.tv_status.setVisibility(View.VISIBLE);
        } else {
            mJsHolder.tv_status.setVisibility(View.INVISIBLE);
        }
        final View temp = mJsHolder.tv_status;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!models.isInstall) {
                    Toasty.error(mContext, "未安装，请先安装该应用再选择").show();
                    return;
                }
                models.isChoose = !models.isChoose;
                if (models.isChoose) {
                    temp.setVisibility(View.VISIBLE);
                } else {
                    temp.setVisibility(View.INVISIBLE);
                }
            }
        });
        mJsHolder.tv_app.setText(models.getAppName());
        Glide.with(parent).load(models.getAppIcon()).into(mJsHolder.iv_app);
        models.isInstall = AppUtils.checkApkExist(mContext, models.getAppPackage());
        if (models.isInstall) {
            mJsHolder.btn_run.setText("运行");
        } else {
            mJsHolder.btn_run.setText("安装");
        }
        mJsHolder.btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!models.isInstall) {
                    AppUtils.launchAppDetail(mContext, models.getAppPackage(), "");
                    return;
                }
                MainApplication.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().setStop(false);
                        TaskManager.getInstance().task(models);
                    }
                });
            }
        });
        mJsHolder.btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskManager.getInstance().stop();
            }
        });

        return convertView;
    }

    public class JsHolder {
        TextView tv_app;
        ImageView iv_app;
        Button btn_run;
        Button btn_stop;
        TextView tv_status;
    }

}
