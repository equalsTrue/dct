package com.dct.model.ck;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/25 15:55
 */

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/25 15:55 
 */
@Entity
@Table(name = "video_detail")
@Data
public class CreatorDetailModel {

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

    @Column(name = "creator")
    private String creator;

    /**
     * 头像
     */
    @Column(name = "profile_picture")
    private String profilePicture;

}
