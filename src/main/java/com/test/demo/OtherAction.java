package com.test.demo;

import com.mvcdiv.annotation.AutoWried;
import com.mvcdiv.annotation.Controller;
import com.mvcdiv.annotation.RequestMapping;
import com.mvcdiv.annotation.RequestParam;
import com.test.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ProjectName: SpringCode
 * @Package: com.test.demo
 * @ClassName: OtherAction
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 15:06
 * @Version: 1.0
 */
@Controller
@RequestMapping("/other")
public class OtherAction {
    @AutoWried
    private IDemoService service;

    @RequestMapping("/query.json")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name) {
        String res = service.get(name);
        try {
            response.getWriter().write(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
