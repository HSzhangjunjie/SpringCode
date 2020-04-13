package com.mvcdiv.annotation;

import java.lang.annotation.*;

/**
 * @ProjectName: SpringCode
 * @Package: com.mvcdiv.annotation
 * @ClassName: RequestParament
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 14:55
 * @Version: 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
