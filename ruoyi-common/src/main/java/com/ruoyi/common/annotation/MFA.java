package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * 多因素验证标签
 *
 * @author ruoyi
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MFA
{
    /** 同一个码能否重复使用 */
    boolean reuse() default true;

    /** 如果没绑定时是否不验证，默认必须 */
    boolean optional() default false;
}