package com.accessibilityservice.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.R;
import com.accessibilityservice.model.AppModel;
import com.accessibilityservice.service.TaskService;
import com.accessibilityservice.util.AppUtils;

import es.dmoral.toasty.Toasty;

public class JsAdapter extends BaseListAdapter<AppModel> {
    public JsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.js_item, null);
            TextView tv_app = convertView.findViewById(R.id.tv_name);
            ImageView iv_app = convertView.findViewById(R.id.iv_icon);
            Button btn_run = convertView.findViewById(R.id.btn_run);
            Button btn_stop = convertView.findViewById(R.id.btn_stop);
            final AppModel models = mList.get(position);
            tv_app.setText(models.getAppName());
            iv_app.setBackgroundResource(models.getAppIcon());
            btn_run.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!AppUtils.isApplicationAvilible(models.getAppPackage())) {
                        Toasty.success(mContext, "请先安装" + models.getAppName());
                        return;
                    }
                    Toasty.normal(mContext, "开始执行" + models.getAppName() + "脚本");
                    AppUtils.startAppByPkg(MainApplication.getContext(), models.getAppPackage());
                    mContext.startService(new Intent(mContext, TaskService.class));
                }
            });
            btn_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainApplication.scheduledThreadPool.shutdownNow();
                    mContext.stopService(new Intent(mContext, TaskService.class));
                    Toasty.success(mContext, "已停止" + models.getAppName() + "脚本");
                }
            });
        }
        return convertView;
    }

}
