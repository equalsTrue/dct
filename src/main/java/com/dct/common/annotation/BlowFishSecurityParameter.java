package com.dct.common.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BlowFish接口加密注解.
 *
 * @author david
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Mapping
@Documented
public @interface BlowFishSecurityParameter {
    /**
     * 出参是否加密，默认加密.
     */
    boolean outEncode() default true;
}
