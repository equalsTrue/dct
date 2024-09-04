package com.dct.model.dct;


import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/***
 * 管理员角色
 * @author Vic.zhao
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_role")
public class AdminRoleModel extends EntityStringIdBase {
	
	private static final long serialVersionUID = -4023769397499664545L;

	/**
	 * 角色名称
	 */
	@Column
	private String roleName;


	@Transient
	private List<String> appIds;

	/**
	 * 备注
	 */
	@Column
	private String remark;

	public AdminRoleModel() {
		// TODO Auto-generated constructor stub
	}

}
