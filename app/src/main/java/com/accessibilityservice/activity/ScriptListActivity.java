package com.accessibilityservice.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.accessibilityservice.R;
import com.accessibilityservice.adapter.JsAdapter;
import com.accessibilityservice.model.JsCategoryModel;
import com.accessibilityservice.model.JsModel;

import org.json.JSONArray;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ScriptListActivity extends BaseActivity {
    private String devId = null;
    private Intent fx;
    private Handler handler = null;
    private JsAdapter jsAdapter;
    private JSONArray jsList;
    private List<JsCategoryModel> list;
    private SweetAlertDialog progressDialog;

    private JsModel getJsItem(String str) {
        for (JsCategoryModel items : this.list) {
            for (JsModel jsModel : items.getItems()) {
                if (str.equals(jsModel.getName())) {
                    return jsModel;
                }
            }
        }
        return null;
    }

    private void showLoading(String str, String str2) {
        this.progressDialog.setTitle((CharSequence) str);
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
        this.jsAdapter = new JsAdapter(getApplicationContext(), this.list);
//        refresh();
    }

    protected void onDestroy() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        super.onDestroy();
    }

    public void onNormalRuns(View view) {
        int i = 0;
        List copyOnWriteArrayList = new CopyOnWriteArrayList();
        for (JsCategoryModel items : this.list) {
            for (JsModel jsModel : items.getItems()) {
                if (jsModel.isSelect()) {
                    copyOnWriteArrayList.add(jsModel);
                }
            }
        }
        if (copyOnWriteArrayList.size() == 0) {
            Toasty.info(this, "请勾选需要执行的脚本", 0, true).show();
            return;
        }
        Toasty.info(this, "开始顺序执行", 0, true).show();
    }

    public void onRandomRuns(View view) {
        int i = 0;
        List copyOnWriteArrayList = new CopyOnWriteArrayList();
        for (JsCategoryModel items : this.list) {
            for (JsModel jsModel : items.getItems()) {
                if (jsModel.isSelect()) {
                    copyOnWriteArrayList.add(jsModel);
                }
            }
        }
        if (copyOnWriteArrayList.size() == 0) {
            Toasty.info(this, "请勾选需要执行的脚本", 0, true).show();
            return;
        }
        Toasty.info(this, "开始随机执行", 0, true).show();
        Collections.shuffle(copyOnWriteArrayList);
    }


    public void refresh() {
        showLoading("正在加载脚本列表..", "预计5 ~ 10秒完成, 请耐心等待..");
    }

    public void reverseSelectAll(View view) {
        for (JsCategoryModel items : this.list) {
            for (JsModel jsModel : items.getItems()) {
                jsModel.setSelect(!jsModel.isSelect());
            }
        }
    }

    public void selectAll(View view) {
        for (JsCategoryModel items : this.list) {
            for (JsModel select : items.getItems()) {
                select.setSelect(true);
            }
        }
    }
}
