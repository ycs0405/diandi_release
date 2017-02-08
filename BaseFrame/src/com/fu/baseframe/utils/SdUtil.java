package com.fu.baseframe.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * sd操作工具
 */
public class SdUtil {
    public static final String FILEDIR = "/studentloan";
    public static final String DATA_DIR = "/studentloan/db";
    public static final String IMG_CAHCE = "/studentloan/Images";
    public static String sddisk = "";

    static {
        getSdpath();
        createAllDir();
    }

    private static void createAllDir() {
        File file = new File(sddisk + FILEDIR);
        if (!file.exists()) {
            file.mkdir();
        }
        File dbFile = new File(sddisk + DATA_DIR);
        if (!dbFile.exists()) {
            dbFile.mkdir();
        }

        File imgFile = new File(sddisk + IMG_CAHCE);
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
    }

    public static boolean getSdpath() {
        if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return false;
        }
        sddisk = Environment.getExternalStorageDirectory().toString();
        return true;
    }

    private static File getFileDir() {
        File file = new File(sddisk + FILEDIR);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    private static File getFileDir(String dir) {
        File file = new File(sddisk + dir);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    /**
     * 获得文件
     *
     * @param name
     * @param b
     * @return
     */
    public static File getFile(String name, boolean b) {
        File file = new File(getFileDir() + "/" + name);
        if (!file.exists() && b) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }

    private static File getFilePath(String name) {
        return new File(getFileDir() + "/" + name);
    }

    public static File getFilePath(String dir, String name) {
        return new File(getFileDir(dir) + "/" + name);
    }

    /**
     * 是否下载过
     *
     * @param name
     * @param count
     * @return true 文件存在；false 文件不存在
     */
    public static boolean isDownload(String name, long count) {
        return getFilePath(name).exists() && getFile(name, false).length() == count;
    }

    public static boolean fileExists(String name) {
        return getFilePath(name).exists();
    }

    public static boolean reNameFile(String name) {
        File oldFile = getFile("1.png", false);
        return oldFile.renameTo(getFilePath(name));
    }

    public static List<String> getFileNames() {
        File file = getFileDir();
        List<String> list = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                list.add(files[i].getName().substring(0, files[i].getName().indexOf(".")));
            }
        }
        return list;
    }
}
