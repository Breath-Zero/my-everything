package com.bittech.everything.core.common;

import com.bittech.everything.core.model.FileType;
import com.bittech.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类：将File对象转换为Thing对象
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-18 14:16
 **/
public final class FileConvertThing {

    // 这是一个辅助类，不希望被别人实例化，不需要继承
    private FileConvertThing() {

    }

    /**
     * 需要想办法把   文件---->Thing
     *
     * @param file
     * @return
     */
    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file)); // 文件深度
        thing.setFileType(computeFileType(file)); // 文件类型
        return thing;
    }

    /**
     * 计算文件的深度
     *
     * @param file
     * @return
     */
    private static int computeFileDepth(File file) {
        int dept = 0;

        String[] segments = file.getAbsolutePath().split("\\\\");
        dept = segments.length;
        return dept;
    }

    /**
     * 计算文件类型
     *
     * @param file
     * @return
     */
    private static FileType computeFileType(File file) {
        // 如果是一个目录，返回OTHER
        if (file.isDirectory()) {
            return FileType.OTHER;
        }
        // 否则返回文件扩展名
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        // 按 . 找
        if (index != -1 && index < fileName.length() - 1) {
            //abc.（文件可能以 . 结尾）
            String extend = fileName.substring(index + 1);
            return FileType.lookup(extend);
        } else {
            return FileType.OTHER;
        }
    }
}
