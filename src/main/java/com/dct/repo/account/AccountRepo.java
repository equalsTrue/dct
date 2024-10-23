package com.dct.repo.account;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/05 17:09
 */

import com.dct.model.dct.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/05 17:09 
 */
public interface AccountRepo extends JpaRepository<AccountModel,String>, JpaSpecificationExecutor<AccountModel> {


    /**
     * 更新账号信息
     * @param uidList
     * @param belongPerson
     * @param userGroup
     * @param country
     * @param time
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update t_account set belong_person =?2 , user_group =?3, country = ?4,assign_status = 1,status = 0,deliver_time = ?5  where uid in (?1) ", nativeQuery = true)
    void assignAccount(List<String> uidList, String belongPerson, String userGroup, String country, String time);

    /**
     * 根据UID查询.
     * @param uid
     * @return
     */
    AccountModel findFirstByUid(String uid);


    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByUid(String uid);

    @Query("select t.creator from AccountModel t")
    List<String> findAllCreator();

    /**
     * 根据belongPerson查询.
     * @param teamMembers
     * @return
     */
    @Query("select t.creator from AccountModel t where t.belongPerson in (?1)")
    List<String> findCreatorByUsers(List<String> teamMembers);


    List<AccountModel> findAllByBelongPerson(String user);
}
