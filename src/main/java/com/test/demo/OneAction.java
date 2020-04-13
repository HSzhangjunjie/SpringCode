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
 * @ClassName: DemoAction
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 14:33
 * @Version: 1.0
 */
@Controller()
@RequestMapping("/one")
public class OneAction {
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

    @RequestMapping("/edit.json")
    public void edit(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) {

    }

    @RequestMapping("/remove.json")
    public void remove(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) {

    }

}
