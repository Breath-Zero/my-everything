package com.bittech.everything.core.index;

import com.bittech.everything.core.interceptor.FileInterceptor;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-17 16:55
 **/
public interface FileScan {
    /**
     * 建立索引
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);
}
