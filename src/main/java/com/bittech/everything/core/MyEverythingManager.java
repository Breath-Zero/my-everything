package com.bittech.everything.core;

import com.bittech.everything.config.MyEverythingConfig;
import com.bittech.everything.core.Search.FileSearch;
import com.bittech.everything.core.Search.impl.FileSearchImpl;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管理者
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-18 17:08
 **/
public class MyEverythingManager {
    private static volatile MyEverythingManager manager;

    private FileSearch fileSearch;

    private FileScan fileScan;

    private ExecutorService executorService; // 程序的调度器


    private MyEverythingManager() {
        this.initComponent();
    }

    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
    }

    public static MyEverythingManager getInstance() {
        if (manager == null) {
            synchronized (MyEverythingManager.class) {
                if (manager == null) {
                    manager = new MyEverythingManager();
                }
            }
        }
        return manager;
    }

    /**
     * 检索
     */
    public List<Thing> search(Condition condition) {
        //NOTICE 扩展点
        return this.fileSearch.search(condition);
    }

    /**
     * 索引
     */
    public void buildIndex() {
        Set<String> directories = MyEverythingConfig.getInstance().getIncludePath();

        if (this.executorService == null) {
            // 采用用目录的个数，作为线程池的固定数量
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0); // 线程id，默认为0

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        System.out.println("Build index start ....");

        for (String path : directories) {
            // 可能有多个磁盘，所以采用多线程方式
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    MyEverythingManager.this.fileScan.index(path);
                    //当前任务完成，值-1
                    countDownLatch.countDown();
                }
            });
        }
        /**
         * 阻塞，一直到任务完成为止，此时值变为0
         */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete ...");
    }
}
