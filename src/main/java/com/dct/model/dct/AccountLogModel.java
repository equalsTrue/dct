package com.dct.model.dct;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 17:10
 */

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * 账号操作日志.
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 17:10 
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_account_log")
public class AccountLogModel extends EntityStringIdBase {


    @Column
    private String uid;

    @Column
    private String creator;
	
    @Column
    private String handler;

    @Column
    private String country;


    /**
     * 前归属人.
     */
    @Column
    private String beforePerson;


    /**
     * 当前归属人.
     */
    @Column
    private String localPerson;


    /**
     * 之前状态(0:正常， 1:封号  2:弃用)
     */
    @Column
    private Integer beforeStatus;


    /**
     * 当前状态(0:正常， 1:封号  2:弃用)
     */
    @Column
    private Integer localStatus;


    /**
     * 操作人.
     */
    @Column
    private String manager;


    /**
     * 更新时间.
     */
    @Column(name="update_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime = new Date(System.currentTimeMillis());

}
