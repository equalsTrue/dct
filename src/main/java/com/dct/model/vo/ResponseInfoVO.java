package com.dct.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Http请求返回结果.
 *
 */
@Data
@Builder
@AllArgsConstructor
public class ResponseInfoVO {

    public ResponseInfoVO() {
    }

    /**
     * 错误码.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer code;

    /**
     * 提示的信息.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String msg;

    /**
     * 具体的内容.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object data;
}
