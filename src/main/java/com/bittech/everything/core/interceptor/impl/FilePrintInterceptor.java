package com.bittech.everything.core.interceptor.impl;

import com.bittech.everything.core.interceptor.FileInterceptor;

import java.io.File;

/**
 * 打印类的具体实现
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-18 15:28
 **/
public class FilePrintInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
