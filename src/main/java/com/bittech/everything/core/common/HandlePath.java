package com.bittech.everything.core.common;

import lombok.Data;

import java.util.Set;

/**
 * 处理文件的目录的变化
 *
 * @Author: Mr.Ye
 * @Data: 2019-02-20 19:02
 **/

@Data
public class HandlePath {
    private Set<String> includePath; // 包含的目录
    private Set<String> excludePath; // 排除的目录
}
