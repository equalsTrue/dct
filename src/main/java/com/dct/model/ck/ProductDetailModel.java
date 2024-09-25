package com.dct.model.ck;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 17:01
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
 * @create: 2024/09/04 17:01 
 */
@Entity
@Table(name = "product_detail")
@Data
public class ProductDetailModel {

    @Id
    @Column(name = "id")
    public String id;


    @Column(name = "date")
    public Long date;

    @Column(name = "url")
    private String url;

    /**
     * 账号(starp/vista)
     */
    @Column(name = "account")
    public String account;


}
