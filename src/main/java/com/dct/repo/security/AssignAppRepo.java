package com.dct.repo.security;

import com.dct.model.dct.AssignAppModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author david
 */
public interface AssignAppRepo extends JpaRepository<AssignAppModel,String>, JpaSpecificationExecutor<AssignAppModel> {

    /**
     * 是否存在该角色分配的app.
     * @param role
     * @return
     */
    boolean existsByRoleId(String role);

    /**
     * 更新关联的App.
     * @param role
     * @param app
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    @Query("update AssignAppModel t set t.app = ?2 where t.roleId = ?1")
    void updateAppList(String role,String app);

    /**
     * 根据角色查询关联App.
     * @param roleId
     * @return
     */
    @Query("select t.app from AssignAppModel t where t.roleId =?1")
    String findAppList(String roleId);

    /**
     * 根据role查询.
     * @param role
     * @return
     */
    @Query("select p.app from AssignAppModel p where p.roleId in ?1")
    List<String> findAppNames(List<String> role);
    @Query(value = "SELECT aa.app " +
            "FROM assign_app aa " +
            "INNER JOIN admin_user_role aur ON aur.role_id = aa.role_id " +
            "INNER JOIN admin_user au ON au.id = aur.user_id " +
            "WHERE au.userName = :username " +
            "LIMIT 1", nativeQuery = true)
    String findAppByUserName(@Param("username") String userName);
    @Query(value = "SELECT au.email, aa.app " +
            "FROM assign_app aa " +
            "INNER JOIN admin_user_role aur ON aur.role_id = aa.role_id " +
            "INNER JOIN admin_user au ON au.id = aur.user_id " +
            "WHERE au.userName in ?1 ", nativeQuery = true)
    List<Object[]> findAssignAppModelByUserName(List<String> userNameList);
}
