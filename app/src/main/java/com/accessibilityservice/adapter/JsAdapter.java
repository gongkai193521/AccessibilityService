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
        JsHolder mJsHolder=null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.js_item, null);
            mJsHolder=new JsHolder();
            mJsHolder.tv_app = convertView.findViewById(R.id.tv_name);
            mJsHolder.iv_app = convertView.findViewById(R.id.iv_icon);
            mJsHolder.btn_run = convertView.findViewById(R.id.btn_run);
            mJsHolder.btn_stop = convertView.findViewById(R.id.btn_stop);
            convertView.setTag(mJsHolder);
        }else{
            mJsHolder=(JsHolder)convertView.getTag();
        }
        final AppModel models = mList.get(position);
        mJsHolder.tv_app.setText(models.getAppName());
        Glide.with(parent).load(models.getAppIcon()).into(mJsHolder.iv_app);
        models.isInstall=AppUtils.checkApkExist(mContext,models.getAppPackage());
        if (models.isInstall){
            mJsHolder.btn_run.setText("运行");
        }else{
            mJsHolder.btn_run.setText("安装");
        }
        mJsHolder.btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!models.isInstall){
                    AppUtils.launchAppDetail(mContext,models.getAppPackage(),"");
                    return;
                }
                Toasty.success(mContext, "开始执行" + models.getAppName() + "脚本").show();
                MainApplication.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().task(models);
                    }
                });
            }
        });
        mJsHolder.btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskManager.isStop = true;
                Toasty.success(mContext, "已停止" + models.getAppName() + "脚本").show();
            }
        });

        return convertView;
    }

    public class JsHolder{
        TextView tv_app;
        ImageView iv_app;
        Button btn_run;
        Button btn_stop;
    }

}
