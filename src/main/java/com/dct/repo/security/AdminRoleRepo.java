package com.dct.repo.security;

import com.dct.model.dct.AdminRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * AdminUserRepo
 *
 * @author Vic on 2019/1/15
 */
public interface AdminRoleRepo extends JpaRepository<AdminRoleModel, String> {

    /**
     * 查询角色名称
     * @param username
     * @return
     */
    @Query(value = "select distinct t.roleName from admin_role t where t.id in (select m.role_id from admin_user_role m left join admin_user u on m.user_id = u.id and u.username=?1 where u.username is not null)", nativeQuery = true)
    List<String> queryRoleNames(String username);

    /**
     * 根据rolename 查询Id
     * @param roleName
     * @return
     */
    @Query(value = "select t.id from AdminRoleModel t where t.roleName = ?1")
    String findByRoleName(String roleName);


    @Query(value = "select u.username from admin_user u where u.id in (select m.user_id from admin_user_role m where m.role_id in (select t.id from admin_role t where t.roleName like CONCAT('%',?1,'%')))",nativeQuery = true)
    List<String> findUserNameByRoleName(String roleName);

    /**
     * 根据role 名称查询id.
     * @param roleNames
     * @return
     */
    @Query(value = "select distinct p.id from admin_role p where p.roleName in ?1", nativeQuery = true)
    List<String> findRoleIds(List<String> roleNames);

    @Query("select t.roleName from AdminRoleModel t")
    List<String> findAllRoleName();
}
