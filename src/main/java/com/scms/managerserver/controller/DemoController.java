package com.scms.managerserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author PSH
 * Date: 2017/12/27
 *
 */
@RestController
public class DemoController {

    @GetMapping("/index")
    public String index () {
        return "hello every one";
    }
}
