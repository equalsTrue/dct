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
 * AdminRolePermissionsModel
 *
 * @author Vic on 2019/1/7
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_role_permissions")
public class AdminRolePermissionsModel extends EntityStringIdBase {

    private static final long serialVersionUID = 5450749660440833641L;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "permissions_id", nullable = false)
    private String permissionsId;
}
