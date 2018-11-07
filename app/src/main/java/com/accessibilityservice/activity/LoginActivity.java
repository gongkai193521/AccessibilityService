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

import com.accessibilityservice.R;
import com.accessibilityservice.manager.UserManager;

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
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    loginSuccess();
                    break;
                default:
                    break;
            }
            super.handleMessage(message);
        }
    };

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

    String loginName;

    private void attemptLogin() {
        mLoginName.setError(null);
        mPasswordView.setError(null);
        loginName = mLoginName.getText().toString();
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
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    private void loginSuccess() {
        hideLoading();
        UserManager.getInstance().saveLoginName(loginName);
        Toasty.success(this, "登录成功", 0, true).show();
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

