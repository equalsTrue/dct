package com.dct.exception;

import lombok.Getter;

/**
 * @author Joetao
 * Created at 2018/8/24.
 */
@Getter
public class CustomException extends RuntimeException{

    private int code;

    public CustomException(){
        super();
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }
}
