package com.dct.model.ck;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 16:55
 */

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program dct  具体视频情况.
 * @description:
 * @author: lichen
 * @create: 2024/09/04 16:55 
 */

@Entity
@Table(name = "video_detail")
@Data
public class VideoDetailModel {

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

    @Column
    public String creator;

    @Column(name = "product_id")
    public String product_id;


    @Column(name = "gmv")
    public Double gmv;

    @Column(name = "video_views")
    public Integer video_views;

    @Column(name = "vid")
    public String vid;

    @Column(name = "commission")
    public Double commission;


    /**
     * 视频链接
     */
    @Column
    public String url;
}
