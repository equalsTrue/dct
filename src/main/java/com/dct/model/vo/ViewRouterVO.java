package com.dct.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Vic on 2019/1/8
 */
@Data
public class ViewRouterVO {
    private String path;

    private String component;

    private Boolean hidden;

    private Boolean leaf;

    private String name;

    /**
     * 操作权限.
     */
    private String operator;

    /**
     * 操作权限路径.
     */
    private String operatorPath;

    private ViewRouterMetaVO meta;

    private String redirect;

    private List<ViewRouterVO> children;
}
