package com.dct.common.constant.enums;

import lombok.Getter;

/**
 * Http请求返回码枚举.
 *
 * @author magic
 * @data 2022/4/19
 */
@Getter
public enum ResponseInfoEnum {

    //未知错误
    UNKNOWN_ERROR(-2, "error"),

    //成功 200，success
    SUCCESS(200, "success"),

    //系统异常
    SYSTEM_ERROR(500, "system error"),

    //成功 200，ok
    OK(200, "OK"),

    //未找到链接
    NOT_FOUND(404, "OK"),

    //错误请求
    BAD_REQUEST(400, "OK"),

    //用户不符合使用要求
//    INVALID_USER(444, "invalid user"),

    //包名不符合使用要求
    INVALID_APP(445, "invalid app");

    private Integer code;

    private String msg;

    ResponseInfoEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
