package com.dct.service.security;

import com.alibaba.fastjson.JSONObject;

import com.dct.model.dct.AdminConfigMapModel;
import com.dct.model.dct.AdminUserModel;
import com.dct.model.vo.AdminUserVO;
import com.dct.model.vo.OptionVO;

import java.util.List;

/**
 * IAdminUserService
 *
 * @author Vic on 2019/1/16
 */
public interface IAdminUserService {

    /**
     * findAllUser
     * @param token
     * @return
     */
    List<AdminUserVO> findAllUser(String token);

    /**
     * 保存用户
     * @param adminUserVO
     */
    void save(AdminUserVO adminUserVO);

    /**
     * 获取用户
     * @param id
     * @return
     */
    AdminUserVO getById(String id);

    /**
     * 获取邮箱
     * @param id
     * @return
     */
    AdminUserVO getEmail(String id);

    /**
     * 重置密码
     * @param adminUserVO
     */
    void resetPassword(AdminUserVO adminUserVO);

    /**
     * 删除
     * @param id
     */
    void deleteById(String id);

    /**
     * 更新邮箱
     * @param id
     * @param email
     */
    void updateEmail(String id, String email);

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    AdminUserVO findUser(String token);

    /**
     * 获取用户的角色
     * @param id
     * @return
     */
    List<String> getRoleIds(String id);

    /**
     * 修改用户角色
     * @param jsonObject
     */
    void updateRoles(JSONObject jsonObject);

    /**
     * 渠道用户账号
     * @param adminUserVO
     */
    void saveChannelUser(AdminUserVO adminUserVO);

    /**根据username查询user
     * @param username
     * @return
     */
    AdminUserModel findUserByUsername(String username);

    /**
     *检测邮箱是否存在
     * @param mail
     * @return
     */
    String checkMail(String mail);

    /**
     * 根据 企业微信userid获取用户
     * @param userId
     * @return
     */
    AdminUserModel getUserByWorkWeChatUserId(String userId);

    /**
     * 修改企业微信用户Id
     * @param token
     * @param userId
     */
    void resetWorkWeChatUserId(String token, String userId);

    List<OptionVO> getAllEmails();

    List<AdminConfigMapModel> getConfigMap(String country);
}
