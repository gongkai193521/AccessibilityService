package com.accessibilityservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.accessibilityservice.R;
import com.accessibilityservice.manager.UserManager;
import com.accessibilityservice.model.Client_User;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.DeviceIdUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

/**
 * @author gongkai
 */
public class LoginActivity extends BaseActivity {
    private AutoCompleteTextView mLoginName;
    private EditText mPasswordView;
    private Button btnLogin;
    private SweetAlertDialog progressDialog;

    @Override
    public int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {
        initView();
    }

    private void setListener() {
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void initView() {
        if (UserManager.getInstance().isLogin()) {
            AppUtils.setLauncherEnabled(this,true);
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
            return;
        }
        setTitle("用户登录");
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mLoginName = findViewById(R.id.login_name);
        mPasswordView = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.email_sign_in_button);
        setListener();
    }


    private void attemptLogin() {
        mLoginName.setError(null);
        mPasswordView.setError(null);
        String loginName = mLoginName.getText().toString();
        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(loginName)) {
            mLoginName.setError(getString(R.string.error_field_required));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            return;
        }
        LoginActivity.this.showLoading("正在登录..", "预计 3 ~ 5 秒完成, 请耐心等待..");
        LoginActivity.this.btnLogin.setText("正在登录..");
        LoginActivity.this.btnLogin.setEnabled(false);


        final AVQuery<AVObject> usernameQuery = new AVQuery<>("Client_User");
        usernameQuery.whereEqualTo("username",loginName);

        final AVQuery<AVObject> passwordQuery = new AVQuery<>("Client_User");
        passwordQuery.whereEqualTo("password",password);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(usernameQuery, passwordQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                LoginActivity.this.btnLogin.setText("登录");
                LoginActivity.this.btnLogin.setEnabled(true);

                hideLoading();
                if (e!=null){
                    Toast.makeText(LoginActivity.this,e.getMessage()+":"+e.getCode(),Toast.LENGTH_LONG).show();
                    return;
                }
                if (list==null||list.size()==0){
                    Toast.makeText(LoginActivity.this,"账号或者密码错误",Toast.LENGTH_LONG).show();
                    return;
                }
                AVObject avObject = list.get(0);
                Client_User mClient_User=new Client_User();
                mClient_User.devices=avObject.getString("devices");
                mClient_User.expiry_date=avObject.getDate("expiry_date").getTime();
                mClient_User.password=avObject.getString("password");
                mClient_User.username=avObject.getString("username");
                mClient_User.max_limit=avObject.getInt("max_limit");

                String mDeviceId=DeviceIdUtils.getDeviceId(LoginActivity.this);

                if (TextUtils.isEmpty(mClient_User.devices)&&mClient_User.max_limit>0){
                    avObject.put("devices",mDeviceId+",");
                    avObject.saveInBackground();
                    loginSuccess(mClient_User);
                }else if (!TextUtils.isEmpty(mClient_User.devices)&&mClient_User.devices.contains(mDeviceId+",")){
                    loginSuccess(mClient_User);
                }else if (!TextUtils.isEmpty(mClient_User.devices)&&!mClient_User.devices.contains(mDeviceId+",")){
                    int count=mClient_User.devices.split(",").length;
                    if (count<mClient_User.max_limit){
                        avObject.put("devices",mClient_User.devices+mDeviceId+",");
                        avObject.saveInBackground();
                        loginSuccess(mClient_User);
                    }else{
                        Toast.makeText(LoginActivity.this,"已超出最大可用设备数",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void loginSuccess(Client_User mmClient_User) {
        UserManager.getInstance().saveLogin(mmClient_User);
        Toasty.success(this, "登录成功", 0, true).show();
        AppUtils.setLauncherEnabled(this,true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
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


}

