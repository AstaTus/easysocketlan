package com.astatus.easysocketlansampleserver.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2015/12/26.
 */
public class FileUtil {
    private static final String TAG = "FileUtils";

    public static String getExtraDir(Context context) {
        return getExtraDir(context, "");
    }

    public static String getExtraDir(Context context, String path) {
        return getExtraDir(context, path, "");
    }

    /**
     * app文件夹 /sdcard/android/data/com.exapple.acp
     *
     * @param context
     * @return
     */
    public static String getExtraDir(Context context, String path, String name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/android");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file.getAbsolutePath() + "/data");
        if (!file1.exists()) {
            boolean mkdir = file1.mkdirs();
        }
        File file2 = new File(file1.getAbsolutePath() + "/" + context.getPackageName()
                + (TextUtils.isEmpty(path) ? "" : ("/" + path)));//路径
        if (!file2.exists()) {
            boolean mkdir = file2.mkdirs();
        }
        File file3 = new File(file2.getAbsolutePath() + (TextUtils.isEmpty(name) ? "" : ("/" + name)));

        return file3.getAbsolutePath();
    }


    /**
     * 获取文件
     *
     * @param context
     * @param name
     * @return
     */
    public static File getSDFile(String name) {
        File file = new File(getSDPath(name));
        return file;
    }

    /* 获取 sdCard 目录下文件夹 或 文件 路径
     * @param context
     * @param name  root/file/fle.txt
     * @return
     */
    public static String getSDPath(String name) {
        String[] split = name.split("/");
        String path = Environment.getExternalStorageDirectory() + "";
        File file = null;
        for (int i = 0; i < split.length; i++) {
            path += "/" + split[i];
            file = new File(path);
            if (i == split.length - 1) {
                if (split[i].contains(".")) {
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                            return file.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "getSDPath: 文件创建失败" + e.toString());
                        }
                    }
                } else {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }

            if (!file.exists()) {
                file.mkdirs();
            }

        }
        return path;
    }

    public static boolean copyFile(File from, File to) {
        try {
            InputStream is = new FileInputStream(from);
            OutputStream os = new FileOutputStream(to);
            byte[] bytes = new byte[1024 * 1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.close();
            is.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }


}
