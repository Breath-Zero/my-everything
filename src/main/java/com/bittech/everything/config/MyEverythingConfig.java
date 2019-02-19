package com.bittech.everything.config;

import lombok.Getter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 配置相关的类
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-18 14:34
 **/

// 配置类应该是一个单例
@Getter
public class MyEverythingConfig {

    private static volatile MyEverythingConfig config;

    /**
     *建立索引文件的路径
     */
    private Set<String> includePath = new HashSet<>(); // 需要提供getter方法

    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>(); // 需要提供getter方法

    // TODO 可配置的参数会在这里体现

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "my_everything";

    private MyEverythingConfig(){ // 1
    }

    // 初始化默认的配置
    private void initDefaultPathsConfig(){
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.includePath.add(path.toString()));
        // 排除的目录
        //windows ： C:\Windows C:\Program Files (x86) C:\Program Files  C:\ProgramData
        //linux : /tmp /etc
        //unix
        String osname = System.getProperty("os.name"); // 得到当前电脑的系统
        if (osname.startsWith("Windows")) {
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files (x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");

        } else {
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }

    public static MyEverythingConfig getInstance(){
        // 采用双重检查
        if (config == null) {
            synchronized (MyEverythingConfig.class) {
                if (config == null) {
                    config = new MyEverythingConfig(); // 应该私有--1
                    config.initDefaultPathsConfig();
                }
            }
        }

        return config;
    }
}
