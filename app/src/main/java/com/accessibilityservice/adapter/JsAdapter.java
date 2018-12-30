package com.accessibilityservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
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
import com.fingerth.supdialogutils.SYSDiaLogUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

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
                    if (models.getmAVFile() == null) {
                        Toasty.error(mContext, "下载地址为空").show();
                        return;
                    }
                    File file = new File(Environment.getExternalStorageDirectory(), models.getmAVFile().getUrl().substring(models.getmAVFile().getUrl().lastIndexOf("/") + 1));
                    FileDownloader.getImpl().create(models.getmAVFile().getUrl())
                            .setPath(file.getAbsolutePath())
                            .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                    Log.i("FileDownloader-pending", "soFarBytes:" + soFarBytes + ",totalBytes:" + totalBytes);
                                    SYSDiaLogUtils.showProgressBar((Activity) mContext, SYSDiaLogUtils.SYSDiaLogType.RoundWidthNumberProgressBar, "下载中...");
                                    SYSDiaLogUtils.setProgressBar(0);
                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                    Log.i("FileDownloader-progress", "soFarBytes:" + soFarBytes + ",totalBytes:" + totalBytes);
                                    SYSDiaLogUtils.setProgressBar((int) ((100F * soFarBytes) / totalBytes));
                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {
                                    Log.i("FileDownloader-complete", "completed");
                                    SYSDiaLogUtils.dismissProgress();
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri uri = Uri.fromFile(new File(task.getPath()));
                                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                    mContext.startActivity(intent);
                                }

                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                    Log.i("FileDownloader-paused", "paused");
                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {
                                    Log.i("FileDownloader-error", e.getMessage());
                                    SYSDiaLogUtils.dismissProgress();
                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {
                                    Log.i("FileDownloader-warn", "warn");
                                }
                            }).start();
                    return;
                }
                MainApplication.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().setStop(false);
                        while (true) {
                            if (TaskManager.getInstance().getStop()) {
                                break;
                            }
                            TaskManager.getInstance().task(models);
                        }
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
