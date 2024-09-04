package com.dct.handler;

import com.dct.constant.enums.ResponseInfoEnum;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.utils.ResponseInfoUtil;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @version 1.0
 * @Author Vic.Zhao
 * @Date 2024/4/23 21:13
 */
@ControllerAdvice
public class BaseExceptionHandler {
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseInfoVO handle(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        return ResponseInfoUtil.error(ResponseInfoEnum.SYSTEM_ERROR.getCode(), fieldError.getDefaultMessage());
    }
}
