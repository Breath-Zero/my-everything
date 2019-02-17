package com.bittech.everything.core.model;

import lombok.Data;

/**
 * @Author: Mr.Ye
 * @Data: 2019-02-15 11:30
 **/
@Data
public class Condition {

    private String name;

    private String fileType;

    private Integer limit; // 限制数量

    /**
     * 检索结果的文件信息depth排序规则
     * 1.默认是true---> asc（升序）
     * 2.false---> desc（降序）
     */
    private Boolean orderByAsc;
}
