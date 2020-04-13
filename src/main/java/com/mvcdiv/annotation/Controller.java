package com.mvcdiv.annotation;

import java.lang.annotation.*;

/**
 * @ProjectName: SpringCode
 * @Package: com.test.annotation
 * @ClassName: Controller
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 14:30
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
