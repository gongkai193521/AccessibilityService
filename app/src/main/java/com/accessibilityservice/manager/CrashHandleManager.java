package com.accessibilityservice.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import com.accessibilityservice.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

/**
 * 获取主线程中的异常信息,并保存到本地
 */
public class CrashHandleManager implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private static CrashHandleManager INSTANCE = new CrashHandleManager();// CrashHandler实例
    private Context mContext;// 程序的Context对象

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandleManager() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandleManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    @SuppressWarnings("static-access")
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true:如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        String currentTime = getCurrentTime(System.currentTimeMillis());
        sb.append(currentTime);
        sb.append(result);
        // 保存文件
        String fileName = "CrashLog.txt";
        File file = FileUtil.getFileOrDir(FileUtil.LOG, fileName);
        if (file.length() > 1000000) {
            file.delete();
            file = FileUtil.getFileOrDir(FileUtil.LOG, fileName);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath(), true);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return String  yyyy/MM/dd HH:mm:ss
     * @throws
     * @Title: getCurrentTime
     * @Description:得到当前时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(long currenttime) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        String format = simple.format(currenttime);
        return format;
    }
}
