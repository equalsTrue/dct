package com.dct.repo.security;

import com.dct.model.dct.AdminRolePermissionsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ：Vic.
 * @ Date       ：Created in 6:22 PM 2019/1/7
 * @ Description：${description}
 * @ Modified By：
 * @Version: $version$
 */
public interface AdminRolePermissionsRepo extends JpaRepository<AdminRolePermissionsModel, String> {

    /**
     * 根据角色id删除关联
     * @param roleId
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    int deleteByRoleId(String roleId);

    /**
     * 查询允许的权限
     * @param id
     * @return
     */
    List<AdminRolePermissionsModel> findAllByRoleId(String id);

    /**
     * 查看是否存在该权限
     * @param roleId
     * @param permissionId
     * @return
     */
    boolean existsByRoleIdAndAndPermissionsId(String roleId, String permissionId);

    /**
     * 根据权限id删除关联
     * @param permissionId
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByPermissionsId(String permissionId);
}
