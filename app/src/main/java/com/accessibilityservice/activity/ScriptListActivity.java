package com.accessibilityservice.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.R;
import com.accessibilityservice.adapter.JsAdapter;
import com.accessibilityservice.manager.TaskManager;
import com.accessibilityservice.model.AppModel;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ScriptListActivity extends BaseActivity {
    private JsAdapter jsAdapter;
    private ArrayList<AppModel> list;
    private SweetAlertDialog progressDialog;
    private ListView lv_list;
    private RefreshLayout rlRefresh;

    private void showLoading(String str, String str2) {
        this.progressDialog.setTitle(str);
        this.progressDialog.setContentText(str2);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    public int setContentView() {
        return R.layout.activity_my_js_list;
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {
        initView();
    }

    private Button btn_reverse_select_all, btn_select_all;

    private void initView() {
        setTitle("已购脚本");
        this.progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        this.progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        lv_list = findViewById(R.id.lv_list);
        btn_select_all = findViewById(R.id.btn_select_all);
        btn_reverse_select_all = findViewById(R.id.btn_reverse_select_all);
        this.rlRefresh = findViewById(R.id.rl_refresh);
        this.rlRefresh.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                getPlatformList();
            }
        });
        this.jsAdapter = new JsAdapter(mContext);
        lv_list.setAdapter(jsAdapter);
        getPlatformList();
    }

    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    public void onNormalRuns(View view) {
        Toasty.info(this, "开始顺序执行", 0, true).show();
        execute();
    }

    public void onRandomRuns(View view) {
        Toasty.info(this, "开始随机执行", 0, true).show();
        execute();
    }


    public void onRefresh(View view) {
        getPlatformList();
    }

    public void getPlatformList() {
        showLoading("正在加载脚本列表..", "请耐心等待..");
        AVQuery<AVObject> mQuery = new AVQuery<>("News_Platform");
        mQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> resList, AVException e) {
                list = new ArrayList<>();
                hideLoading();
                rlRefresh.finishRefresh();
                for (AVObject mAVObject : resList) {
                    AppModel appModel = new AppModel();
                    String strJson = mAVObject.getString("data");
                    List<AppModel.AppPageModel> mAppPages = new Gson().fromJson(strJson, new TypeToken<List<AppModel.AppPageModel>>() {
                    }.getType());
                    appModel.setPages(mAppPages);
                    appModel.setPlanTime(mAVObject.getLong("platform_plan"));
                    appModel.setAppIcon(mAVObject.getString("platform_logo"));
                    appModel.setAppPackage(mAVObject.getString("platform_package"));
                    appModel.setAppName(mAVObject.getString("platform_name"));
                    list.add(appModel);
                }

                jsAdapter.setList(list);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (jsAdapter != null) {
            jsAdapter.notifyDataSetChanged();
        }
    }

    private void hideLoading() {
        this.progressDialog.hide();
    }

    private void release() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

    public void reverseSelectAll(View view) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (AppModel mAppModel : list) {
            mAppModel.isChoose = !mAppModel.isChoose;
        }
        jsAdapter.notifyDataSetChanged();
    }

    public void selectAll(View view) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (AppModel mAppModel : list) {
            mAppModel.isChoose = true;
        }
        jsAdapter.notifyDataSetChanged();
    }

    private void execute() {
        if (list == null || list.size() == 0) {
            Toasty.error(mContext, "脚本列表为空").show();
            return;
        }
        MainApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                for (AppModel models : list) {
                    if (models.isChoose) {
                        TaskManager.getInstance().task(models);
                    }
                }
            }
        });
    }
}