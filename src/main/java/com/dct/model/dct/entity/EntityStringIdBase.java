package com.dct.model.dct.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * EntityBase
 *
 * @author Vic on 2019/1/7
 */
@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public class EntityStringIdBase implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    @Column(name="createtime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date(System.currentTimeMillis());

    public Date getCreateTime() {
        if(null == this.createTime){
            return null;
        }
        return (Date) createTime.clone();
    }

    public void setCreateTime(Date createTime) {
        if(null == createTime) {
            this.createTime = null;
        }else{
            this.createTime = (Date) createTime.clone();
        }
    }
}
