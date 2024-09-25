package com.dct.model.dct;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 10:31
 */

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 样品.
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 10:31 
 */

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_product")
public class ProductModel extends EntityStringIdBase {

    @Column(name = "product_name")
    private String productName;


    @Column(name = "picture")
    private String picture;

    /**
     * 数量.
     */
    @Column(name = "count")
    private Integer count;

    /**
     * 申请数量.
     */
    @Column(name = "apply_count")
    private Integer applyCount;

    /**
     * 管理人
     */
    @Column
    private String manager;

    /**
     * 样品颜色.
     */
    @Column
    private String color;

    /**
     * 区域.
     */
    @Column
    private String region;

    /**
     * 存放地点.
     */
    @Column(name = "store_location")
    private String storageLocation;

    @Column
    private String pid;



    /**
     * 样品状态(0:已申样，1:在库 2:申请中 3:拍摄中 )
     */
    @Column
    private Integer status;

    /**
     * 使用人
     */
    @Column
    private String user;

    /**
     * 出库申请(0:未申请 1:已申请)
     */
    @Column(name = "out_apply")
    private Integer outApply;


    /**
     * 是否批阅:0:未审批 1:已审核,2已拒绝
     */
    @Column(name = "is_approval")
    private Integer isApproval;

    /**
     * 申请人.
     */
    @Column(name = "apply_user")
    private String applyUser;
}
