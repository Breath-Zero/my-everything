package com.bittech.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-15 11:05
 **/
public class DataSourceFactory {
    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {

    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url,username,password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //获取当前工程路径
                    String workDir = System.getProperty("user.dir");
                    dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "my_everything");
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase() {
        //1.获取数据源
        //2.获取SQL语句
        InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("my_everything.sql");
        //3.获取数据库连接和名称执行SQL
    }

    public static void main(String[] args) {

        DataSourceFactory.initDatabase();

    }

}
