package com.scms.managerserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.List;

/**
 * @author PSH
 * Date: 2017/12/27
 *
 */
@RestController
public class DemoController {

    @GetMapping("/dumpThreadInfo")
    public List<ThreadInfo> index () {
        List<ThreadInfo> threadInfos = Arrays.asList(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));

        return threadInfos;
    }

    @GetMapping("/getThreadInfo")
    public List<ThreadInfo> threadInfos () {
        List<ThreadInfo> threadInfos = Arrays.asList(ManagementFactory.getThreadMXBean().getThreadInfo(new long[]{11192,13648,13590,501,13589,11737}));

        return threadInfos;
    }
}
