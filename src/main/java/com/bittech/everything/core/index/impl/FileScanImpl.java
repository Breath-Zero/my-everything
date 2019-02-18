package com.bittech.everything.core.index.impl;

import com.bittech.everything.config.MyEverythingConfig;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-17 16:52
 **/
public class FileScanImpl implements FileScan {

    // 排除工具类
    private MyEverythingConfig config = MyEverythingConfig.getInstance();

    // 遍历完成后调用一下文件拦截器
    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        // 给一个路径，遍历所有的文件夹
        File file = new File(path);
        if (file.isFile()) { // 如果是文件
            //D:\a\b\abc.pdf  ->  D:\a\b
            // 如果a\b\被排除，则整个大的路径也就被排除了
            if (config.getExcludePath().contains(file.getParentFile())) {
                return;
            }
        } else { // 是目录
            if (config.getExcludePath().contains(path)) {
                // 如果排除目录包含这个路径，就直接返回
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }
        // 遍历完直接调用拦截器

        // File Directory  (文件或者目录)
        for (FileInterceptor interceptor : this.interceptors) {
            interceptor.apply(file);
        }
        // 文件变为Thing-->写入数据库
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
