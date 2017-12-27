package com.scms.managerserver.model;

import lombok.Data;

/**
 * @author PSH
 * Date: 2017/12/27
 * cpu的一些信息
 */
@Data
public class CpuInfo {
    public static final int coreCpuNum;

    static {
        coreCpuNum = Runtime.getRuntime().availableProcessors();
    }

    /**
     * cpu的使用率
     */
    private int cpuUsage;


}
