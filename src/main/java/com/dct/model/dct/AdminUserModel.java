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
 * 管理员用户
 * @author YeahMobi
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_user", uniqueConstraints = {
		@UniqueConstraint(name = "unique_username", columnNames = { "username" }),
		@UniqueConstraint(name = "unique_email", columnNames = { "email" }) })
public class AdminUserModel extends EntityStringIdBase {

	@Column
	private String username;
	
	@Column
	private String password;
	
	@Column
	private String email;

	@Column(name = "work_we_chat_user_id")
	private String workWeChatUserId;
}
