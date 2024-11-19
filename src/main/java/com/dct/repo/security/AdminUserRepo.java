package com.dct.repo.security;

import com.dct.model.dct.AdminUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * AdminUserRepo
 *
 * @author Vic on 2019/1/15
 */
public interface AdminUserRepo extends JpaRepository<AdminUserModel, String> {

    /**
     * 根据用户名查找
     * @param username
     * @return
     */
    AdminUserModel findFirstByUsername(String username);

    /**
     * 重置
     * @param password
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update AdminUserModel set password=?1 where id=?2")
    void resetPassword(String password, String id);


    /**
     * 更新邮箱
     * @param email
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update AdminUserModel set email=?1 where id=?2")
    void updateEmail(String email, String id);

    /**
     * 根据username查询
     * @param username
     * @return
     */
    @Query(value = "select t from AdminUserModel t where t.username = ?1")
    AdminUserModel findUserByUsername(String username);

    /**
     * 检查邮箱
     * @param mail
     * @return
     */
    @Query("select t.username from AdminUserModel t where t.email = ?1")
    List<String> checkMail(String mail);

    /**
     * 获取用户
     * @param workWeChatUserId
     * @return
     */
    AdminUserModel findFirstByEmail(String workWeChatUserId);

    /**
     * 获取全部用户名
     * @return
     */
    @Query("select t.username from AdminUserModel t ")
    List<String> getAllUserName();

    /**
     * 根据用户名查ID
     * @return
     */
    @Query("select t.username from AdminUserModel t where t.id = ?1")
    List<String> getUsername(String id);

}
