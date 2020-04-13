package com.mvcdiv.annotation;

import java.lang.annotation.*;

/**
 * @ProjectName: SpringCode
 * @Package: com.mvcdiv.annotation
 * @ClassName: Service
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 14:56
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
