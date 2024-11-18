package com.dct.repo.security;

import com.dct.model.dct.AdminUserRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AdminUserRepo
 *
 * @author Vic on 2019/1/15
 */
public interface AdminUserRoleRepo extends JpaRepository<AdminUserRoleModel, String> {

    /**
     * 查找角色列表
     * @param id
     * @return
     */
    List<AdminUserRoleModel> findAllByUserId(String id);

    /**
     * 是否存在该用户的角色
     * @param userId
     * @param roleId
     * @return
     */
    boolean existsByUserIdAndRoleId(String userId, String roleId);

    /**
     * 删除角色关联
     * @param userid
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByUserId(String userid);

    /**
     * 删除角色关联
     * @param roleid
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByRoleId(String roleid);

}
