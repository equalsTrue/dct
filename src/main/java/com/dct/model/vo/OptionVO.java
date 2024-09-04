package com.dct.model.vo;

import lombok.Data;

/**
 * @author Charles
 * @date 2019/2/21
 * @description :
 */
@Data
public class OptionVO {

    String id;

    String label;

    String value;

    public OptionVO() {
    }
    // 添加构造函数
    public OptionVO(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
