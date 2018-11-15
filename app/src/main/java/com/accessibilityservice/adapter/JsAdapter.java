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
import com.bumptech.glide.Glide;

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
            Glide.with(parent).load(models.getAppIcon()).into(iv_app);
            btn_run.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toasty.success(mContext, "开始执行" + models.getAppName() + "脚本").show();
                    MainApplication.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            TaskManager.getInstance().task(models);
                        }
                    });
                }
            });
            btn_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TaskManager.isStop = true;
                    Toasty.success(mContext, "已停止" + models.getAppName() + "脚本").show();
                }
            });
        }
        return convertView;
    }

}
