package com.bittech.everything.core.interceptor;

import java.io.File;

/**
 * 拦截器
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-18 15:25
 **/

@FunctionalInterface // 函数式接口，一个接口中只有一个方法
public interface FileInterceptor {
    void apply(File file);
}
