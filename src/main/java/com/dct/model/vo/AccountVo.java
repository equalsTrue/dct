package com.dct.model.vo;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 17:06
 */

import lombok.Data;

import javax.persistence.Column;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 17:06 
 */

@Data
public class AccountVo {

    private String uid;

    private String creator;

    private String country;

    /**
     * 分配状态(0:可分配状态，1:已分配状态)
     */
    private Integer assignStatus;

    /**
     * 归属人.
     */
    private String belongPerson;


    /**
     * 归属人组别.
     */
    private String userGroup;

    /**
     * 状态(0:正常， 1:封号  2:弃用)
     */
    private Integer status;

    /**
     * 分配人.
     */
    private String manager;
}
