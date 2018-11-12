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
import com.accessibilityservice.util.MyThread;
import com.accessibilityservice.util.Threads;

import es.dmoral.toasty.Toasty;

public class JsAdapter extends BaseListAdapter<AppModel> {
    Threads threads = new Threads();

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
                        Toasty.warning(mContext, "请先安装" + models.getAppName()).show();
                        return;
                    }
                    Toasty.success(mContext, "开始执行" + models.getAppName() + "脚本").show();
                    AppUtils.startAppByPkg(MainApplication.getContext(), models.getAppPackage());
                    threads.task(new Threads.Fn() {
                        @Override
                        public void onRun(MyThread thread) {
                            super.onRun(thread);
                            TaskManager.getInstance().doTask(thread.isRun());
                        }
                    });
                }
            });
            btn_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!MainApplication.getExecutorService().isShutdown()) {
                        MainApplication.getExecutorService().shutdownNow();
                    }
                    Toasty.success(mContext, "已停止" + models.getAppName() + "脚本").show();
                    threads.release();
                }
            });
        }
        return convertView;
    }

}
