package com.bittech.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-15 11:05
 **/
public class DataSourceFactory {
    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    /**
     * 私有的构造方法
     */
    private DataSourceFactory() {

    }

    // 采用多重检查，多线程环境下仍然是一个，不会出现线程竞争
    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC
                    dataSource.setDriverClassName("org.h2.Driver");// 一个字符串就可以实例化（通过反射做到）

                    //url,username,password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口

                    //JDBC规范中关于MySql jdbc:mysql://ip:port/databaseName

                    //获取当前工程路径
                    String workDir = System.getProperty("user.dir");

                    //JDBC规范中关于H2 jdbc:h2:filepath--->存储到本地文件
                    dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "my_everything");
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2.获取SQL语句

        //不采取读取绝对路径的文件  例：D:\IdeaProjects\Maven\my-everything
        //采取读取classpath路径下的文件

        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("my_everything.sql");) {

            if (in == null) {
                throw new RuntimeException("Not read init database script please check it");
            }
            // 把输入流--->String

            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {

                String line =null;
                while ((line = reader.readLine()) != null) { // 表述没有读到末尾
                    if (!line.startsWith("--")) { // 排除 --开始的注释内容
                        sqlBuilder.append(line); // 添加到sqlBuilder
                    }

                }

            }
            //3.获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString(); // 把输入流--->String
            //JDBC编程
            //3.1 获取数据库的链接
            Connection connection = dataSource.getConnection();
            //3.2 创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3 执行SQL语句
            statement.execute();
            //3.4 关闭链接
            connection.close();
            statement.close();

        } catch (IOException e) {

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

//    public static void main(String[] args) {
////        检测数据源是否创建成功
////        DataSource dataSource = DataSourceFactory.dataSource();
////        System.out.println(dataSource);
//
//        DataSourceFactory.initDatabase();
//
//    }

}
