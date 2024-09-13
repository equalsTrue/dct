package com.dct.model.ck;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @version 1.0
 * @Author david.li
 * @Date 2024/5/30 18:32
 */
@Entity
@Table(name = "t_gmv_detail")
@Data
public class GmvDetailModel {
    @Id
    @Column(name = "id")
    public String id;

    /**
     * 账号(starp/vista)
     */
    @Column(name = "account")
    public String account;

    @Column(name = "date")
    public Long date;

    @Column(name = "country")
    public Long  country;

    @Column(name = "campaign_id")
    public String campaign_id;

    @Column(name = "campaign_name")
    public String campaign_name;

    @Column(name = "product_id")
    public String product_id;

    @Column(name = "product_name")
    public String product_name;

    @Column(name = "orders")
    public String orders;

    @Column(name = "gmv")
    public String gmv;

    @Column(name = "video_views")
    public Integer video_views;

    @Column(name = "videos")
    public String videos;

    @Column(name = "commission")
    public String commission;

    @Column(name = "level_1_category")
    public String level_1_category;

    @Column(name = "level_2_category")
    public String level_2_category;

    @Column(name = "belong_person")
    public BigDecimal belong_person;

}