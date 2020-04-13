package com.test.service.impl;

import com.mvcdiv.annotation.Service;
import com.test.service.IDemoService;

/**
 * @ProjectName: SpringCode
 * @Package: com.test.service.impl
 * @ClassName: DemoServiceImpl
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 15:14
 * @Version: 1.0
 */
@Service("/service")
public class DemoServiceImpl implements IDemoService {
    @Override
    public String get(String name) {
        return "name is : " + name;
    }
}
