package com.mvcdiv.annotation;

import java.lang.annotation.*;

/**
 * @ProjectName: SpringCode
 * @Package: com.mvcdiv.annotation
 * @ClassName: AutoWrited
 * @Author: ZhangJunjie
 * @Description:
 * @Date: 2020/4/13 14:52
 * @Version: 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWried {
    String value() default "";
}
