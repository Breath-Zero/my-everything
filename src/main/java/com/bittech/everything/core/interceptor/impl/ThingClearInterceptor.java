package com.bittech.everything.core.interceptor.impl;

import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.interceptor.ThingInterceptor;
import com.bittech.everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 删除文件后的清理工作
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-19 11:43
 **/
public class ThingClearInterceptor implements ThingInterceptor, Runnable {

    // 需要一个容器（采用队列）---存放删除的文件
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    // 调用数据库
    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing); // 添加到队列中
    }


    // 用一个线程来实现文件的删除
    @Override
    public void run() {
        // 取得一个值并删除
        Thing thing = this.queue.poll();
        if (thing != null) {
            fileIndexDao.delete(thing);
        }
        //1.优化 批量删除----delete改为批量删除
        //List<Thing> thingList = new ArrayList<>();
        try {
            Thread.sleep(100); // 删除一个后睡一下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
