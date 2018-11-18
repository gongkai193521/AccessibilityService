package com.accessibilityservice.model;

import java.util.List;

/**
 * Created by gongkai on 2018/11/7.
 */

public class AppModel {
    public boolean isInstall;
    private String appPackage;
    private String appName;
    private String appIcon;
    private Long planTime;//计划时间
    private Long executeTime; //已经在执行的时间
    private List<AppPageModel> mPages;//页面

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public Long getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Long planTime) {
        this.planTime = planTime;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public List<AppPageModel> getPages() {
        return mPages;
    }

    public void setPages(List<AppPageModel> pages) {
        mPages = pages;
    }

    public class AppPageModel {
        private String className;//页面类名
        private String type;  //操作 1 列表  2 详情页
        private Long planTime; //执行时间   0代表永久
        private List<String> views;//控件

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getPlanTime() {
            return planTime;
        }

        public void setPlanTime(Long planTime) {
            this.planTime = planTime;
        }

        public List<String> getViews() {
            return views;
        }

        public void setViews(List<String> views) {
            this.views = views;
        }
    }
}
