package com.accessibilityservice.model;

import java.util.List;

/**
 * Created by gongkai on 2018/11/7.
 */

public class AppModel {
    private String appPackage;
    private String appName;
    private int  appIcon;
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

    public int getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(int appIcon) {
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
        private String opt;  //操作 1 点击  2 滑动
        private Long executeTime; //执行时间   0代表永久
        private List<ViewModel> mViewModels;//控件

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getOpt() {
            return opt;
        }

        public void setOpt(String opt) {
            this.opt = opt;
        }

        public Long getExecuteTime() {
            return executeTime;
        }

        public void setExecuteTime(Long executeTime) {
            this.executeTime = executeTime;
        }

        public List<ViewModel> getViewModels() {
            return mViewModels;
        }

        public void setViewModels(List<ViewModel> viewModels) {
            mViewModels = viewModels;
        }
    }

    public class ViewModel {
        private String viewId;//控件id
        private String opt; //控件行为 1 点击 2 滑动

        public String getViewId() {
            return viewId;
        }

        public void setViewId(String viewId) {
            this.viewId = viewId;
        }

        public String getOpt() {
            return opt;
        }

        public void setOpt(String opt) {
            this.opt = opt;
        }
    }
}
