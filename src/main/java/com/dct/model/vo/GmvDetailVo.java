package com.dct.model.vo;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/05 11:54
 */

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/05 11:54 
 */
@Data
public class GmvDetailVo {

    public Integer index;

    public String account;

    public String date;

    public String country;

    public String creator;

    public String campaign_id;

    public String campaign_name;

    public String product_id;

    public String product_name;

    public String productUrl;

    public String orders;

    public Double gmv;

    public Integer video_views;

    public Integer videos;

    public String creator_commission;

    public String partner_commission;

    public String level_1_category;

    public String level_2_category;

    public String belong_person;

    public String userGroup;

    public Integer addVideos;


}
