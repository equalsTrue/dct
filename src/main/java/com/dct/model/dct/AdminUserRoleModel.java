package com.dct.model.dct;

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/***
 * 管理员用户角色
 * @author Vic.zhao
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_user_role", uniqueConstraints = {
		@UniqueConstraint(name = "unique_userId_roleId", columnNames = {"user_id", "role_id"})
})
public class AdminUserRoleModel extends EntityStringIdBase {

	/**
	 * 用户ID
	 */
	@Column(name = "user_id", columnDefinition = "varchar(36) not null")
	private String userId;

	/**
	 * 角色id
	 */
	@Column(name = "role_id", columnDefinition = "varchar(36) not null")
	private String roleId;

	public AdminUserRoleModel(){
		
	}
}
