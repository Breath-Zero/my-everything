package com.bittech.everything.core.Search;

import com.bittech.everything.core.Search.impl.FileSearchImpl;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-17 16:20
 **/
public interface FileSearch {
    /**
     * 根据condition条件进行数据库的检索
     *
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

//    public static void main(String[] args) {
//        FileSearch fileSearch = new FileSearchImpl(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
//
//        List<Thing> list = fileSearch.search(new Condition());
//        System.out.println(list);
//    }
}
