package com.bittech.everything.core.Search.impl;

import com.bittech.everything.core.Search.FileSearch;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;

/**
 * 具体实现检索功能的业务
 * @Author: Mr.Ye
 * @Data: 2019-02-17 16:25
 **/
public class FileSearchImpl implements FileSearch {

    // 数据源的初始化，三种方式：1.立即初始化 2.构造方法 3.构造块
    //
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {

        return this.fileIndexDao.search(condition);
    }
}
