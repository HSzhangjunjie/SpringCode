package com.mvcdiv.annotation;

import java.lang.annotation.*;

/**
 * @ProjectName: SpringCode
 * @Package: com.mvcdiv.annotation
 * @ClassName: RequestMapping
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 14:50
 * @Version: 1.0
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
