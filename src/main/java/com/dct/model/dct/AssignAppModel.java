package com.dct.model.dct;

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * App分配列表.
 * @author david
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "assign_app")
public class AssignAppModel extends EntityStringIdBase {

    /**
     * 角色.
     */
    @Column(name = "role_id")
    private String roleId;


    /**
     * 分配的App.
     */
    @Column(name = "app",columnDefinition = "text")
    private String app;

}
