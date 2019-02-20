package com.bittech.everything.cmd;

import com.bittech.everything.config.MyEverythingConfig;
import com.bittech.everything.core.MyEverythingManager;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-15 10:58
 **/
public class MyEverythingCmdAPP {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // 解析用户参数
        parseParams(args);

//        System.out.println("这是MyEverything应用程序的命令行交互程序");
        // 欢迎
        welcome();

        // 统一调度器
        MyEverythingManager manager = MyEverythingManager.getInstance();

        // 启动后台清理线程
        manager.startBackgroundClearThread();

        // 交互式
        interactive(manager);

    }

    private static void parseParams(String[] args) {

        MyEverythingConfig config = MyEverythingConfig.getInstance();

        /**
         * 处理参数：
         * 如果用户指定的参数格式不对，使用默认值即可
         */
        for (String param : args) {

            /**
             * 返回值，参数
             */
            String maxReturnParam = "--maxReturn=";
            if (param.startsWith(maxReturnParam)) {
                //--maxReturn=value
                int index = param.indexOf("="); // 按照“=”截取，得到需要的值
                String maxReturnStr = param.substring(index + 1);
                try {
                    int maxReturn = Integer.parseInt(maxReturnStr); // 得到返回值
                    config.setMaxReturn(maxReturn);
                } catch (NumberFormatException e) {
                    //如果用户指定的参数格式不对，使用默认值即可
                }
            }

            /**
             * 排序，参数
             */
            String deptOrderByAscParam = "--deptOrderByAsc=";
            if (param.startsWith(deptOrderByAscParam)) {
                //--deptOrderByAsc=value
                int index = param.indexOf("=");
                String deptOrderByAscStr = param.substring(index + 1);
                config.setDeptOrderAsc(Boolean.parseBoolean(deptOrderByAscStr));
            }

            /**
             * 包含目录，参数
             * 可能以 ; 结尾
             */
            String includePathParam = "--includePath=";
            if (param.startsWith(includePathParam)) {
                //--includePath=values (;)--通过分号分割
                int index = param.indexOf("=");
                String includePathStr = param.substring(index + 1);
                String[] includePaths = includePathStr.split(";");
                if (includePaths.length > 0) {
                    config.getIncludePath().clear();
                }
                for (String p : includePaths) {
                    config.getIncludePath().add(p);
                }
            }

            /**
             * 排除的目录，参数
             */
            String excludePathParam = "--excludePath=";
            if (param.startsWith(includePathParam)) {
                //--excludePath=values (;)
                int index = param.indexOf("=");
                String excludePathStr = param.substring(index + 1);
                String[] excludePaths = excludePathStr.split(";");
                config.getExcludePath().clear();
                for (String p : excludePaths) {
                    config.getExcludePath().add(p);
                }
            }
        }
    }

    private static void interactive(MyEverythingManager manager) {
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void search(MyEverythingManager manager, Condition condition) {
        System.out.println("检索功能");

        // 查询之前，先设置返回数量和排序方式
        condition.setLimit(MyEverythingConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(MyEverythingConfig.getInstance().getDeptOrderAsc());

        //统一调度器中的search
        //name fileType limit orderByAsc
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {
            System.out.println(thing.getPath());
        }
    }

    private static void index(MyEverythingManager manager) {
        //统一调度器中的index
        new Thread(manager::buildIndex).start();
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，My Everything...");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }
}
