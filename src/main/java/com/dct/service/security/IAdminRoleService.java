package com.dct.service.security;

import com.alibaba.fastjson.JSONObject;
import com.dct.model.dct.AdminRoleModel;

import java.util.List;

/**
 * @author Charles
 * @date 2019/2/12
 * @description :
 */
public interface IAdminRoleService {

    /**
     * 查找所有角色
     * @param token
     * @return
     */
    List<AdminRoleModel> findAllRole(String token);

    /**
     * 根据主键获取
     * @param id
     * @return
     */
    AdminRoleModel findById(String id);

    /**
     * 保存角色
     * @param jsonObject
     */
    void save(JSONObject jsonObject);

    /**
     * 删除角色
     * @param id
     */
    void delete(String id);

    /**
     * 获取允许的权限
     * @param id
     * @return
     */
    List<String> getEnablePermission(String id);


    /**
     * 根据权限查询分配的App.
     * @param params
     * @return
     */
    List<String> queryRoleAppInfo(JSONObject params);

}
