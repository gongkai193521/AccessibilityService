package com.accessibilityservice.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.accessibilityservice.R;
import com.accessibilityservice.adapter.JsAdapter;
import com.accessibilityservice.model.AppModel;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ScriptListActivity extends BaseActivity {
    private JsAdapter jsAdapter;
    private ArrayList<AppModel> list;
    private SweetAlertDialog progressDialog;
    private ListView lv_list;

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

    private void initView() {
        setTitle("已购脚本");
        this.progressDialog = new SweetAlertDialog(this, 5);
        this.progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        lv_list = findViewById(R.id.lv_list);
        this.jsAdapter = new JsAdapter(mContext);
        lv_list.setAdapter(jsAdapter);
        list = new ArrayList<>();
        AppModel appModel = new AppModel();
        appModel.setAppName("微鲤看看");
        appModel.setAppPackage("cn.weli.story");
        appModel.setAppIcon(R.drawable.wlkk);
        list.add(appModel);
        AppModel appModel1 = new AppModel();
        appModel1.setAppName("极速头条");
        appModel1.setAppPackage("com.ss.android.article.lite");
        appModel1.setAppIcon(R.drawable.jstt);
        list.add(appModel1);
        jsAdapter.setList(list);

    }

    protected void onDestroy() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        super.onDestroy();
    }

    public void onNormalRuns(View view) {
        Toasty.info(this, "开始顺序执行", 0, true).show();
    }

    public void onRandomRuns(View view) {
        Toasty.info(this, "开始随机执行", 0, true).show();
    }


    public void refresh() {
        showLoading("正在加载脚本列表..", "预计5 ~ 10秒完成, 请耐心等待..");
    }

    public void reverseSelectAll(View view) {
    }

    public void selectAll(View view) {
    }
}
