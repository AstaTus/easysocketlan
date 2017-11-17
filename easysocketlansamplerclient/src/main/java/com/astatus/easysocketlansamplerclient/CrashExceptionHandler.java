package com.astatus.easysocketlansamplerclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/5.
 */
public class CrashExceptionHandler implements UncaughtExceptionHandler {
    //MyApp中配置
//    private CrashExceptionHandler exceptionHandler;
//    exceptionHandler = new CrashExceptionHandler(getApplicationContext());
//    Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    private Context context;
    private File file;
    private Class activity;


    public CrashExceptionHandler(Context context, File file, Class activity) {
        this.context = context;
        this.file = file;
        this.activity = activity;
    }

    public void setClass(Class activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StackTraceElement[] elements = ex.getStackTrace();
        for(StackTraceElement e : elements){
            e.toString();
        }
//        File file = new File(FileUtils.getSDPath(context, path));
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = null;
        OutputStream os = null;
        baos = new ByteArrayOutputStream();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        String version = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = ("\n\n---" + format.format(new Date()) + "---"
                + "\n-------version:" + version
                + "\n-------device: " + Build.DEVICE
                + "\n-------model: " + Build.MODEL
                + "\n-------cpu1: " + Build.CPU_ABI
                + "\n-------cpu2: " + Build.CPU_ABI2
                + "\n-------SDK:" + Build.VERSION.SDK
                + "\n-------system:" + Build.VERSION.RELEASE + "\n\n").getBytes();
        baos.write(bytes, 0, bytes.length);
        PrintStream printStream = new PrintStream(baos);
        ex.printStackTrace(printStream);
        Throwable throwable = ex.getCause();
        while (throwable != null) {
            throwable.printStackTrace(printStream);
            throwable = throwable.getCause();
        }
        printStream.close();
        if (fileWriter != null) {
            byte[] bytes1 = baos.toByteArray();
            try {
                fileWriter.write(new String(bytes1), 0, bytes1.length);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (activity != null) {
                Intent intent = new Intent(context, activity);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            System.exit(-1);
        }
    }
}
