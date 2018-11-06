package com.accessibilityservice.model;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;

import es.dmoral.toasty.Toasty;

public class JsModel implements Serializable {
    private String appUrl;
    private Object attach;
    private String code;
    private String desc;
    private String icon;
    private boolean installDisabled;
    private boolean isActive;
    private boolean isAlone;
    private Boolean isInstall;
    private boolean isSelect;
    private boolean isSyncVer;
    private Boolean joinStatistics;
    private String localVersionName;
    private int moneyScale;
    private String name;
    private String pkgName;
    private Integer pollingInterval;
    private String resources;
    private boolean runDisabled;
    private Integer status;
    private boolean stopDisabled;
    private Integer timeType = Integer.valueOf(0);
    private float todayMoney;
    private Integer todayRunDuration = Integer.valueOf(0);
    private Integer todayRunDurationX = Integer.valueOf(0);
    private float todayRunPv;
    private String todayRunPvStr;
    private Integer todayRunTotal = Integer.valueOf(0);
    private Integer todayRunTotalX = Integer.valueOf(0);
    private Integer triggerRefreshTodayScoreCount = Integer.valueOf(0);
    private String versionName;

    public static void setBgColor(View view, String str) {
        view.setBackgroundColor(Color.parseColor(str));
    }

    public static void setLayoutHeight(View view, int i) {
    }

    public static void setTextColor(View view, String str) {
        ((TextView) view).setTextColor(Color.parseColor(str));
    }


    public String getAppUrl() {
        return this.appUrl;
    }

    public Object getAttach() {
        return this.attach;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getIcon() {
        return this.icon;
    }

    public Boolean getInstall() {
        return this.isInstall;
    }

    public Boolean getJoinStatistics() {
        return this.joinStatistics;
    }

    public String getLocalVersionName() {
        return this.localVersionName;
    }

    public int getMoneyScale() {
        return this.moneyScale;
    }

    public String getName() {
        return this.name;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public Integer getPollingInterval() {
        return this.pollingInterval;
    }

    public String getResources() {
        return this.resources;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Integer getTimeType() {
        return this.timeType;
    }

    public float getTodayMoney() {
        return this.todayMoney;
    }

    public Integer getTodayRunDuration() {
        return this.todayRunDuration;
    }

    public Integer getTodayRunDurationX() {
        return this.todayRunDurationX;
    }

    public float getTodayRunPv() {
        return this.todayRunPv;
    }

    public String getTodayRunPvStr() {
        return TextUtils.isEmpty(this.todayRunPvStr) ? "--" : this.todayRunPvStr;
    }

    public Integer getTodayRunTotal() {
        return this.todayRunTotal;
    }

    public Integer getTodayRunTotalX() {
        return this.todayRunTotalX;
    }

    public Integer getTriggerRefreshTodayScoreCount() {
        return this.triggerRefreshTodayScoreCount;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isAlone() {
        return this.isAlone;
    }

    public boolean isFinish() {
        return 100.0f == getTodayRunPv();
    }

    public boolean isInstallDisabled() {
        return this.installDisabled;
    }

    public boolean isRunDisabled() {
        return this.runDisabled;
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    public boolean isStopDisabled() {
        return this.stopDisabled;
    }

    public boolean isSyncVer() {
        return this.isSyncVer;
    }

    public void onInstallApp(final View view) {
        final Button button = (Button) view;
        button.setEnabled(false);
        button.setText("准备中..");
        final Handler anonymousClass1 = new Handler() {
            public void handleMessage(Message message) {
                try {
                    JSONObject jSONObject = new JSONObject(message.getData().getString("s"));
                    String string = jSONObject.getString("operation");
                    if ("start".equals(string)) {
                        button.setText("正在安装..");
                        super.handleMessage(message);
                    } else if ("callback".equals(string)) {
                        string = jSONObject.getString("data");
                        button.setText("下载中:" + string + "%");
                        super.handleMessage(message);
                    } else if ("end".equals(string)) {
                        button.setText("安装");
                        button.setEnabled(true);
                        if (jSONObject.getBoolean("data")) {
                            Toasty.success(view.getContext(), "安装成功", 0).show();
                            super.handleMessage(message);
                        }
                        Toasty.error(view.getContext(), "安装失败", 0).show();
                        super.handleMessage(message);
                    } else {
                        if (NotificationCompat.CATEGORY_ERROR.equals(string)) {
                            Toasty.error(view.getContext(), "安装异常", 0).show();
                            button.setText("安装");
                            button.setEnabled(true);
                        }
                        super.handleMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void onItemClick(View view) {
        setSelect(!isSelect());
    }

    public void setActive(boolean z) {
        this.isActive = z;
    }

    public void setAlone(boolean z) {
        this.isAlone = z;
    }

    public void setAppUrl(String str) {
        this.appUrl = str;
    }

    public void setAttach(Object obj) {
        this.attach = obj;
    }

    public void setCode(String str) {
        this.code = str;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public void setInstall(Boolean bool) {
        this.isInstall = bool;
    }

    public void setInstallDisabled(boolean z) {
        this.installDisabled = z;
    }

    public void setJoinStatistics(Boolean bool) {
        this.joinStatistics = bool;
    }

    public void setLocalVersionName(String str) {
        this.localVersionName = str;
    }

    public void setMoneyScale(int i) {
        this.moneyScale = i;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setPkgName(String str) {
        this.pkgName = str;
    }

    public void setPollingInterval(Integer num) {
        this.pollingInterval = num;
    }

    public void setResources(String str) {
        this.resources = str;
    }

    public void setRunDisabled(boolean z) {
        this.runDisabled = z;
    }

    public void setSelect(boolean z) {
        this.isSelect = z;
    }

    public void setStatus(Integer num) {
        this.status = num;
        setRunDisabled(false);
        setStopDisabled(false);
    }

    public void setStopDisabled(boolean z) {
        this.stopDisabled = z;
    }

    public void setSyncVer(boolean z) {
        this.isSyncVer = z;
    }

    public void setTimeType(Integer num) {
        this.timeType = num;
    }

    public void setTodayMoney(float f) {
        this.todayMoney = f;
    }

    public void setTodayRunDuration(Integer num) {
        this.todayRunDuration = num;
    }

    public void setTodayRunDurationX(Integer num) {
        this.todayRunDurationX = num;
        if (getTimeType().intValue() == 0 && getTodayRunDuration().intValue() != 0) {
            setTodayRunPv(Float.parseFloat(new DecimalFormat("0.0").format((double) ((((float) num.intValue()) / ((float) getTodayRunDuration().intValue())) * 100.0f))));
            setTodayRunPvStr(getTodayRunDurationX() + " /" + getTodayRunDuration() + " (秒)");
        }
    }

    public void setTodayRunPv(float f) {
        this.todayRunPv = f;
    }

    public void setTodayRunPvStr(String str) {
        this.todayRunPvStr = str;
    }

    public void setTodayRunTotal(Integer num) {
        this.todayRunTotal = num;
    }

    public void setTodayRunTotalX(Integer num) {
        this.todayRunTotalX = num;
        if (1 == getTimeType().intValue() && getTodayRunTotal().intValue() != 0) {
            setTodayRunPv(Float.parseFloat(new DecimalFormat("0.0").format((double) ((((float) num.intValue()) / ((float) getTodayRunTotal().intValue())) * 100.0f))));
            setTodayRunPvStr(getTodayRunTotalX() + " /" + getTodayRunTotal() + " (次)");
        }
    }

    public void setTriggerRefreshTodayScoreCount(Integer num) {
        this.triggerRefreshTodayScoreCount = num;
    }

    public void setVersionName(String str) {
        this.versionName = str;
    }


}
