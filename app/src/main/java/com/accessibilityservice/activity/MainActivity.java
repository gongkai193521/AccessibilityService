package com.accessibilityservice.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.accessibilityservice.BuildConfig;
import com.accessibilityservice.MainApplication;
import com.accessibilityservice.R;
import com.accessibilityservice.manager.UserManager;
import com.accessibilityservice.service.TaskService;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.DeviceIdUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

/**
 * Created by gongkai on 2018/11/6.
 */

public class MainActivity extends BaseActivity {
    private int[] icons = new int[]{R.drawable.myjs, R.drawable.refresh,
            R.drawable.updated, R.drawable.mctrl, R.drawable.settings,
            R.drawable.mobile, R.drawable.clear, R.drawable.at, R.drawable.ideal};
    private String[] names = new String[]{"已购脚本", "刷新引擎", "检测更新", "手控管理",
            "应用详情", "设备标识", "清理缓存", "反馈建议", "敬请期待"};
    private SweetAlertDialog progressDialog;
    private TextView tv_active_state, tv_active_welcom, tv_active_version;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    MainActivity.this.hideLoading();
                    Toasty.info(MainActivity.this, "当前已是最新版本", 0, true).show();
                    break;
                case 1:
                    MainActivity.this.hideLoading();
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MainActivity.this, -1);
                    sweetAlertDialog.setTitleText("确认对话框").setContentText("检测到新版本, 本次更新内容如下: \r\n" + "， 是否确认更新?")
                            .setConfirmText("立即更新").setCancelText("取消更新")
                            .showCancelButton(true).setCancelClickListener(null)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.hide();
                                }
                            }).show();
                    break;
                case 2:
                    loadIcon(false);
                    Toasty.error(MainActivity.this, "脚本服务启用失败, 请重试", 0, true).show();
                    MainActivity.this.hideLoading();
                    break;
                case 3:
                    loadIcon(true);
                    MainActivity.this.hideLoading();
                    break;
                case 4:
                    UserManager.getInstance().reset();
                    MainActivity.this.hideLoading();
                    MainActivity.this.finish();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    Toasty.info(MainActivity.this, "退出登录成功", 0, true).show();
                    break;
                case 5:
                    Toasty.success(MainActivity.this, "清理完毕", 0, true).show();
                    MainActivity.this.hideLoading();
                    break;
                default:
                    break;
            }
            super.handleMessage(message);
        }
    };

    @Override
    public int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {
        initView();
    }

    boolean isActive;

    private void initView() {
        setTitle("控制台");
//        updatedExpireIn();
        this.progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        this.progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        startService();
        GridView gridView = findViewById(R.id.gv_list);
        tv_active_state = findViewById(R.id.tv_active_state);
        tv_active_welcom = findViewById(R.id.tv_active_welcom);
        tv_active_welcom.setText("欢迎您，" + UserManager.getInstance().getLoginname());
        tv_active_version = findViewById(R.id.tv_active_version);
        tv_active_version.setText("引擎版本：" + BuildConfig.VERSION_NAME);
        List arrayList = new ArrayList();
        for (int i = 0; i < icons.length; i++) {
            HashMap hashMap = new HashMap();
            hashMap.put("icon", this.icons[i]);
            hashMap.put("name", this.names[i]);
            arrayList.add(hashMap);
        }
        gridView.setAdapter(new SimpleAdapter(this, arrayList, R.layout.menu_item,
                new String[]{"icon", "name"}, new int[]{R.id.iv_icon, R.id.tv_name}));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long j) {
                switch (i) {
                    case 0:
                        if (!MainApplication.getUiManager().check()) {
                            Toasty.normal(mContext, "请找到 “辅助” 开启辅助功能服务");
                            return;
                        }
                        startActivity(new Intent(mContext, ScriptListActivity.class));
                        if (!isActive) {
                            Toasty.error(MainActivity.this, "脚本引擎未激活", 0, true).show();
                        }
                        break;
                    case 1:
                        startService();
                        break;
                    case 2:
                        MainActivity.this.checkUpdated();
                        return;
                    case 3:
                        Toasty.error(MainActivity.this, "手控服务未激活, 请重试", 0, true).show();
                        break;
                    case 4:
                        AppUtils.openDetail(MainActivity.this, MainActivity.this.getPackageName());
                        return;
                    case 5:
                        ((ClipboardManager) MainActivity.this.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", DeviceIdUtils.getDeviceId(mContext)));
                        Toasty.info(MainActivity.this, "已复制剪切板", 0, true).show();
                        return;
                    case 6:
                        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MainActivity.this, -1);
                        sweetAlertDialog.setTitleText("确认对话框").setContentText("此操作将会清空全部统计, 是否继续?")
                                .setConfirmText("确定").setCancelText("取消")
                                .showCancelButton(true).setCancelClickListener(null)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.hide();
                                        MainActivity.this.showLoading("正在清理缓存..", "预计5 ~ 10秒完成, 请耐心等待..");
                                        handler.sendEmptyMessageDelayed(5, 3000);
                                    }
                                }).show();
                        return;
                    case 8:
                        Toasty.info(MainActivity.this, "敬请期待..", 0, true).show();
                        return;
                    default:
                        return;
                }
            }
        });
    }


    private void checkUpdated() {
        showLoading("检测更新", "检测中..");
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    public void loadIcon(boolean z) {
        ImageView imageView = findViewById(R.id.iv_active);
        isActive = z;
        if (z) {
            imageView.setBackgroundResource(R.drawable.ok);
            tv_active_state.setText("脚本引擎已激活");
        } else {
            imageView.setBackgroundResource(R.drawable.no);
            tv_active_state.setText("脚本引擎未激活");
        }
    }

    private void release() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

    private void showLoading(String str, String str2) {
        this.progressDialog.setTitleText(str);
        this.progressDialog.setContentText(str2);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    private void hideLoading() {
        this.progressDialog.hide();
    }

    private void startService() {
        showLoading("正在启动脚本引擎...", "预计10 ~ 20秒完成, 请耐心等待..");
        handler.sendEmptyMessageDelayed(3, 5000);
    }

//    private void updatedExpireIn() {
//        try {
//            String string = this.userInfoSp.getString("expireInFormat", "0");
//            if ("0".equals(string)) {
//                this.mainBinding.setExpireIn("终身使用");
//                return;
//            }
//            this.mainBinding.setExpireIn("服务到期：" + string);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    public void onLogoff(View view) {
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, -1);
        sweetAlertDialog.setTitleText("退出登陆").setContentText("是否确认退出?")
                .setConfirmText("确定")
                .setCancelText("取消")
                .showCancelButton(true)
                .setCancelClickListener(null)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.hide();
                        MainActivity.this.logoff();
                    }
                }).show();
    }

    private void logoff() {
        showLoading("提示", "正在退出, 请耐心等候..");
        handler.sendEmptyMessageDelayed(4, 3000);
    }

}
