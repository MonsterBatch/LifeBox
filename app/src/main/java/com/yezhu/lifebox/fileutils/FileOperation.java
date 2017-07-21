package com.yezhu.lifebox.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yuyifei on 17-7-18.
 */

public class FileOperation {
    public static boolean copyFile(String srcPath, String destPath) {
        boolean flag = false;

        File srcFile = new File(srcPath);
        if (!srcFile.exists()) { // 源文件不存在
            System.out.println("源文件不存在");
            return false;
        }

        String destDir = destPath.substring(0, destPath.lastIndexOf("/"));
        if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
            System.out.println("源文件路径和目标文件路径重复!");
            return false;
        }
        File destFile = new File(destPath);
        if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
            System.out.println("目标目录下已有同名文件!");
            return false;
        }

        File destFileDir = new File(destDir);
        destFileDir.mkdirs();
        try {
            FileInputStream fis = new FileInputStream(srcPath);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();

            flag = true;
        } catch (IOException e) {
            //
        }

        if (flag) {
            System.out.println("复制文件成功!");
        }

        return flag;
    }
}
