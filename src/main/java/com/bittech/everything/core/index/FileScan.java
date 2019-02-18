package com.bittech.everything.core.index;

import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-17 16:55
 **/
public interface FileScan {
    /**
     * 建立索引
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);


    public static void main(String[] args) {
        DataSourceFactory.initDatabase();
        FileScan scan = new FileScanImpl();
        FileInterceptor interceptor = new FilePrintInterceptor();
        scan.interceptor(interceptor);

        FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
        scan.interceptor(fileIndexInterceptor);

        scan.index("E:\\KuGou");
    }
}
