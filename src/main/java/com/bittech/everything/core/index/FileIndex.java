package com.bittech.everything.core.index;

import com.bittech.everything.core.model.Thing;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-17 16:55
 **/
public interface FileIndex {
    /**
     * 建立索引
     * @param thing
     */
    void index(Thing thing);
}
