package com.bittech.everything.core;

import com.bittech.everything.config.MyEverythingConfig;
import com.bittech.everything.core.Search.FileSearch;
import com.bittech.everything.core.Search.impl.FileSearchImpl;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.interceptor.impl.ThingClearInterceptor;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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


    /**
     * 清理删除的文件---Thing的拦截器
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread; // 删除文件需要一个线程
    // 表示变量--默认为false
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    private MyEverythingManager() {
        this.initComponent();
    }

    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        // 检查当前数据库空间是否有数据库
        //检查数据库
        checkDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        // 调用拦截器
        // 发布代码的时候是不需要的
//        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        // Thing的拦截器初始化
        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true); // 设置为守护线程
    }

    private void checkDatabase() {
        String fileName = MyEverythingConfig.getInstance().getH2IndexPath() + ".mv.db";
        File dbFile = new File(fileName);
        if (dbFile.isFile() && !dbFile.exists()) {
            DataSourceFactory.initDatabase();
        }
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
        //NOTICE 扩展----文件的删除
        //Stream 流式处理 JDK8
        return this.fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    // 如果文件不存在
                    if (!flag) {
                        //做删除操作----使用到生产者消费者模型
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;

                }).collect(Collectors.toList());
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
            this.executorService.submit(() -> {
                MyEverythingManager.this.fileScan.index(path);
                //当前任务完成，值-1
                countDownLatch.countDown();
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

    /**
     * 启动清理线程
     */
    public void startBackgroundClearThread() {
        if (this.backgroundClearThreadStatus.compareAndSet(false, true)) {
            this.backgroundClearThread.start();
        } else {
            System.out.println("不能重复启用清理线程");
        }
    }
}
