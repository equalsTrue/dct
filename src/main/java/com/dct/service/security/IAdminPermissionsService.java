package com.dct.service.security;


import com.dct.model.dct.AdminPermissionsModel;
import com.dct.model.dct.AdminUserModel;
import com.dct.model.vo.PermissionVO;
import com.dct.model.vo.ViewRouterVO;

import java.util.List;

/**
 * @author Charles
 */
public interface IAdminPermissionsService {

	/**
	 * 登录处理
	 * @param username
	 * @param password
	 * @return
	 */
	String login(String username, String password);

	/**
	 * 退出处理
	 * @param token
	 */
	void logout(String token);

	/**
	 * 编辑权限
	 * @param model
	 * @return
	 */
	AdminPermissionsModel editPermissions(AdminPermissionsModel model);

	/**
	 * 根据ID获取权限
	 *
	 * @param permissionsId
	 * @return
	 */
	AdminPermissionsModel getPermissions(String permissionsId);

	/**
	 * 删除权限
	 *
	 * @param permissionsId
	 */
	void removePermissions(String permissionsId);

	/**
	 * 保存权限
	 *
	 * @param model
	 * @return
	 */
	AdminPermissionsModel savePermissions(AdminPermissionsModel model);

	/**
	 * 查询权限列表
	 *
	 * @param searchModel
	 * @return
	 */
	List<AdminPermissionsModel> getPermissionsList(AdminPermissionsModel searchModel);

	/**
	 * 获取带次级权限的权限列表
	 * @param roleId
	 * @return
	 */
	List<AdminPermissionsModel> getPermissionsListWithSub(String roleId);

	/**
	 * 获取父级菜单列表(不为FUNCTION的权限)
	 * @return
	 */
	List<AdminPermissionsModel> getParentPermissionsList();

	/**
	 * 编辑角色权限
	 * @param roleId
	 * @param permissionsId
	 */
	void editRolePermission(String roleId, String[] permissionsId);

	/**
	 * 查找所有权限
	 * @return
	 */
	List<PermissionVO> findAllPermission();

	/**
	 * 查找具体权限信息
	 * @param id
	 * @return
	 */
	AdminPermissionsModel findById(String id);

	/**
	 * 保存权限
	 * @param adminPermissionsModel
	 */
	void save(AdminPermissionsModel adminPermissionsModel);

	/**
	 * 删除权限
	 * @param id
	 */
	void delete(String id);


	/**
	 * 获取用户菜单(用户无权限则菜单不显示)
	 *
	 * @param userName
	 * @return
	 */
	List<ViewRouterVO> getUserPermittedMenuList(String userName);

	/**
	 * 获取角色
	 * @param token
	 * @return
	 */
	List<String> getRoleNames(String token);

	/**
	 * 获取用户名称
	 * @param token
	 * @return
	 */
	String getUserName(String token);

	/**
	 * 根据用户名登陆
	 * @param userModel
	 * @return
	 */
    String login(AdminUserModel userModel);
}
