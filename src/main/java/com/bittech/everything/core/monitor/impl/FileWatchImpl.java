package com.bittech.everything.core.monitor.impl;

import com.bittech.everything.core.common.FileConvertThing;
import com.bittech.everything.core.common.HandlePath;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-20 19:05
 **/
public class FileWatchImpl implements FileWatch, FileAlterationListener {

    private FileIndexDao fileIndexDao;

    private FileAlterationMonitor monitor;

    /**
     * 实现文件的监控
     * @param fileIndexDao
     */
    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10); // 检查的时间间隔
    }

    /**
     * 文件的监听
     * @param fileAlterationObserver
     */
    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
//        observer.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryDelete " + directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("onDirectoryDelete " + directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete " + directory);
    }

    @Override
    public void onFileCreate(File file) {
        //文件创建
        System.out.println("onFileCreate " + file);
        this.fileIndexDao.insert(FileConvertThing.convert(file)); // File-->Thing  插入数据库
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange " + file);
    }

    @Override
    public void onFileDelete(File file) {
        //文件删除
        System.out.println("onFileDelete " + file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
//        observer.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlePath handlePath) {
        //监控的是includePath集合
        for (String path : handlePath.getIncludePath()) {
            FileAlterationObserver observer = new FileAlterationObserver(
                    path, pathname -> {
                String currentPath = pathname.getAbsolutePath(); // 得到当前目录
                for (String excludePath : handlePath.getExcludePath()) {
                    if (excludePath.startsWith(currentPath)) { // 如果不包含排除文件，返回false
                        return false;
                    }
                }
                return true;
            });
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
