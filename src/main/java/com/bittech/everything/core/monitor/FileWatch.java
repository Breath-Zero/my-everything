package com.bittech.everything.core.monitor;

import com.bittech.everything.core.common.HandlePath;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-20 18:59
 **/
public interface FileWatch {
    /**
     * 监听启动
     */
    void start();

    /**
     * 监听的目录
     */
    void monitor(HandlePath handlePath);

    /**
     * 监听停止
     */
    void stop();
}
