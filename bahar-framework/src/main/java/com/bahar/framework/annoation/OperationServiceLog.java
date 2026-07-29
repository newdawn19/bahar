package com.bahar.framework.annoation;

import java.lang.annotation.*;

/**
 * 操作日志记录注解
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationServiceLog {
    String description() default "";
}
