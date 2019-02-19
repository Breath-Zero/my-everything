package com.bittech.everything.core.interceptor;

import com.bittech.everything.core.model.Thing;

/**
 * 拦截器--用于文件删除
 * @Author: Mr.Ye
 * @Data: 2019-02-19 11:41
 **/
@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
