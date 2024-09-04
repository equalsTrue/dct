package com.dct.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Charles
 * @date 2019/2/13
 * @description :
 */
@Data
public class PermissionVO {
    /**
     * id
     */
    private String id;
    /**
     * 树图节点名称
     */
    private String label;
    /**
     * 是否为叶节点
     */
    private Boolean leaf;
    /**
     * 是否可选
     */
    private Boolean disabled;
    private List<PermissionVO> children;
}
