package com.dct.model.dct;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/05 17:00
 */

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/05 17:00 
 */

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_account")
public class AccountModel extends EntityStringIdBase {

    @Column
    private String uid;

    @Column
    private String creator;

    @Column
    private String country;

    /**
     * 分配状态(0:可分配状态，1:已分配状态)
     */
    @Column(name = "assign_status")
    private Integer assignStatus;

    /**
     * 归属人.
     */
    @Column(name = "belong_person")
    private String belongPerson;


    /**
     * 归属人组别.
     */
    @Column(name = "user_group")
    private String userGroup;

    /**
     * 状态(0:正常， 1:封号  2:弃用)
     */
    @Column
    private Integer status;

    /**
     * 分配人.
     */
    @Column
    private String manager;


    /**
     * 交付日期
     */
    @Column(name = "deliver_time")
    private String deliverTime;

    /**
     * 封号日期.
     */
    @Column(name = "close_time")
    private String closeTime;


    /**
     * 弃号日期.
     */
    @Column(name = "terminate_time")
    private String terminateTime;

}
