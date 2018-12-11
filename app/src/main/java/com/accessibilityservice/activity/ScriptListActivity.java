package com.accessibilityservice.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.R;
import com.accessibilityservice.adapter.JsAdapter;
import com.accessibilityservice.manager.TaskManager;
import com.accessibilityservice.manager.UserManager;
import com.accessibilityservice.model.AppModel;
import com.accessibilityservice.util.TimeUtil;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

    private static ScriptListActivity mScriptListActivity;

    public static ScriptListActivity getActivity(){
        return mScriptListActivity;
    }

    public Long getMaxTime(){
        if (tv_endTime==null||tv_endTime.getTag()==null||tv_endTime==null||tv_endTime.getTag()==null)return null;
        String[] split = ((String) tv_endTime.getTag()).split(":");
        Calendar calendar = Calendar.getInstance();
        if (TimeUtil.parseTime((String) tv_startTime.getTag())>TimeUtil.parseTime((String) tv_endTime.getTag())){
            calendar.add(Calendar.DATE,1);
        }
        calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(split[0]));
        calendar.set(Calendar.MINUTE,Integer.valueOf(split[1]));
        calendar.set(Calendar.SECOND,Integer.valueOf(split[2]));
        return calendar.getTimeInMillis();
    }


    @Override
    public int setContentView() {
        return R.layout.activity_my_js_list;
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {
        mScriptListActivity=this;
        initView();
    }

    private Button btn_reverse_select_all, btn_select_all,btn_right;

    private TextView tv_startTime,tv_endTime;

    private void initView() {
        setTitle("已购脚本");
        tv_startTime=findViewById(R.id.tv_startTime);
        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseTime(tv_startTime);
            }
        });
        tv_endTime=findViewById(R.id.tv_endTime);
        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseTime(tv_endTime);
            }
        });
        btn_right=findViewById(R.id.btn_right);
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_startTime.getTag()==null||tv_endTime.getTag()==null){
                    Toasty.normal(mContext, "请先设置时间!").show();
                    return;
                }
                String[] split = ((String) tv_startTime.getTag()).split(":");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(split[0]));
                calendar.set(Calendar.MINUTE,Integer.valueOf(split[1]));
                calendar.set(Calendar.SECOND,Integer.valueOf(split[2]));
                if (mHandler.hasMessages(200)){
                    mHandler.removeMessages(200);
                }
                if (calendar.getTimeInMillis()<System.currentTimeMillis()){
                    long delay=24*60*60 * 1000-(System.currentTimeMillis()-calendar.getTimeInMillis());
                    mHandler.sendEmptyMessageDelayed(200,delay);
                }else{
                    long delay=calendar.getTimeInMillis()-System.currentTimeMillis();
                    mHandler.sendEmptyMessageDelayed(200,delay);
                }
                Toasty.normal(mContext, "开启成功!").show();
            }
        });
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

    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onRandomRuns(null);
            mHandler.sendEmptyMessageDelayed(200,24*60*60 * 1000);
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        release();
        if (mHandler.hasMessages(200)){
           mHandler.removeMessages(200);
        }
    }

    public void onNormalRuns(View view) {
        execute(false);
    }

    public void onRandomRuns(View view) {
        execute(true);
    }


    public void onRefresh(View view) {
        getPlatformList();
    }

    public void getPlatformList() {
        showLoading("正在加载脚本列表...", "预计3 ~ 5秒完成, 请耐心等待..");
        AVQuery<AVObject> mQuery = new AVQuery<>("News_Platform");
        mQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> resList, AVException e) {
                if (resList == null) {
                    Toasty.info(mContext, "数据列表为空，请检查数据网络");
                    return;
                }
                list = new ArrayList<>();
                hideLoading();
                rlRefresh.finishRefresh();
                for (AVObject mAVObject : resList) {
                    AppModel appModel = new AppModel();
                    String strJson = mAVObject.getString("data");
                    List<AppModel.AppPageModel> mAppPages = new Gson().fromJson(strJson, new TypeToken<List<AppModel.AppPageModel>>() {
                    }.getType());
                    appModel.setmAVFile(mAVObject.getAVFile("apk"));
                    appModel.setDownload_url(mAVObject.getString("download_url"));
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
            if (mAppModel.isInstall) {
                mAppModel.isChoose = !mAppModel.isChoose;
            }
        }
        jsAdapter.notifyDataSetChanged();
    }

    //全选
    public void selectAll(View view) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (AppModel mAppModel : list) {
            if (mAppModel.isInstall) {
                mAppModel.isChoose = true;
            }
        }
        jsAdapter.notifyDataSetChanged();
    }

    //已选择列表
    private List<AppModel> chooseList = new ArrayList<>();

    private void execute(boolean isRandom) {
        if (System.currentTimeMillis() > UserManager.getInstance().getLogin().expiry_date) {
            Toasty.normal(mContext, "服务已到期,请续费!").show();
            return;
        }
        chooseList.clear();
        for (AppModel mAppModel : list) {
            if (mAppModel.isInstall && mAppModel.isChoose) {
                chooseList.add(mAppModel);
            }
        }
        if (chooseList.size() == 0) {
            Toasty.error(mContext, "请先选择要执行的脚本").show();
            return;
        }
        if (isRandom) {
            List<AppModel> mtempLists=new ArrayList<>();
            mtempLists.addAll(chooseList);
            chooseList.clear();
            for (int i=0;i<100;i++) {
                Collections.shuffle(mtempLists);
                chooseList.addAll(mtempLists);
            }
            Toasty.info(mContext, "开始随机执行", 0, true).show();
        } else {
            Toasty.info(mContext, "开始顺序执行", 0, true).show();
        }
        TaskManager.getInstance().setStop(false);
        MainApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    if (TaskManager.getInstance().getStop()) {
                        break;
                    }
                    for (AppModel models : chooseList) {
                        if (TaskManager.getInstance().getStop()) {
                            break;
                        }
                        if (models.isChoose) {
                            TaskManager.getInstance().task(models);
                        }
                    }
                }
            }
        });
    }


    private void showChooseTime(final TextView mTextView){
        Object tag = mTextView.getTag();
        if(tag==null){
            tag="00:00:00";
        }
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(new Date(TimeUtil.parseTime((String)tag)));
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY,0);
        startDate.set(Calendar.MINUTE,0);
        startDate.set(Calendar.SECOND,0);
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.HOUR_OF_DAY,23);
        endDate.set(Calendar.MINUTE,59);
        endDate.set(Calendar.SECOND,59);
        //正确设置方式 原因：注意事项有说明
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Calendar mDate = Calendar.getInstance();
                mDate.setTime(date);
                int hour=mDate.get(Calendar.HOUR_OF_DAY);
                int miute=mDate.get(Calendar.MINUTE);
                int second=mDate.get(Calendar.SECOND);
                String time=(hour<10?"0"+hour:hour+"")+":"+(miute<10?"0"+miute:miute+"")+":"+(second<10?"0"+second:second+"");

                if (mTextView.getId()==R.id.tv_startTime){
                    if (time.equals("00:00:00")){
                        mTextView.setText("开始时间");
                        mTextView.setTag(null);
                        return;
                    }else {
                        mTextView.setText("开始时间:" + time);
                    }
                }else{
                    if (time.equals("00:00:00")){
                        mTextView.setText("结束时间");
                        mTextView.setTag(null);
                        return;
                    }else {
                        mTextView.setText("结束时间:" + time);
                    }
                }
                mTextView.setTag(time);
            }
        })
                .setType(new boolean[]{false, false, false, true, true,true})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentTextSize(15)//滚轮文字大小
                .setTitleSize(15)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
        pvTime.show();
    }
}