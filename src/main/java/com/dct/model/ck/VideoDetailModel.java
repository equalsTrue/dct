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



    @Column(name = "campaign_id")
    public String cid;

    @Column(name = "product_id")
    public String pid;


    @Column(name = "affiliate_gmv")
    public String gmv;

    @Column(name = "video_views")
    public Integer videoViews;

    @Column(name = "vid")
    public String vid;

    @Column(name = "commission")
    public String commission;

    /**
     * 视频链接
     */
    @Column
    public String url;
}
