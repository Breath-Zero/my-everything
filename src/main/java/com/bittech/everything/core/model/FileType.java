package com.bittech.everything.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-15 11:31
 **/
public enum FileType {
    IMG("png", "jpeg", "jpe", "gif"),
    DOC("ppt", "pptx", "doc", "docx", "pdf"),
    BIN("exe", "sh", "jar", "msi"),
    ARCHIVE("zip", "rar"),
    OTHER("*");

    /**
     * 对应的文件类型的扩展名集合
     */
    private Set<String> extend = new HashSet<>();

    FileType(String... extend) {
        this.extend.addAll(Arrays.asList(extend));
    }


    /**
     * 根据文件扩展名获取文件类型
     *
     * @param extend
     * @return
     */
    public static FileType lookup(String extend) {
        for (FileType fileType : FileType.values()) {
            if (fileType.extend.contains(extend)) {
                return fileType;
            }
        }
        return FileType.OTHER;
    }


}