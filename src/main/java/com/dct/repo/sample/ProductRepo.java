package com.dct.repo.sample;

import com.dct.model.dct.ProductModel;
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
 * @create: 2024/09/12 11:33
 */
public interface ProductRepo extends JpaRepository<ProductModel,String>, JpaSpecificationExecutor<ProductModel> {

    /**
     * 更新产品图片.
     * @param pid
     * @param s3Url
     */
    @Query("update ProductModel set picture = ?2 where pid = ?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void updatePictureUrl(String pid, String s3Url);

    /**
     * 更新申请样品.
     * @param id
     * @param applyCount
     * @param applyUser
     */
    @Query("update ProductModel set applyCount = ?2,applyUser = ?3,status = 1,outApply = 1,isApproval = 0 where id = ?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void applyProduct(String id,Integer applyCount, String applyUser);

    /**
     * 查询需要审批.
     * @param pid
     * @return
     */
    @Query("select t.isApproval,t.status,t.outApply from ProductModel t where t.pid = ?1 group by t.status,t.outApply,t.isApproval")
    List<Object[]> checkStatusAndApplyStatus(List<String> pid);

    /**
     * 部分产品拍摄中.
     * @param id
     * @param count
     */
    @Query("UPDATE ProductModel p SET p.count = ?2, p.applyCount = 0, p.outApply = 0 WHERE p.id = ?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void updateApprove(String id, Integer count);

    /**
     * 更新所有数量拍摄中.
     *
     * @param id
     * @param count
     * @param applyCount
     */
    @Query("update ProductModel t set t.count = ?2,t.applyCount = ?3,t.status = 3,t.isApproval = 1 where t.id = ?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void updateAllApprove(String id, Integer count, Integer applyCount);

    /**
     * 查询在库状态的样品.
     * @param pid
     * @param color
     * @return
     */
    @Query("select t from ProductModel t where t.pid = ?1 and t.color = ?2 and t.status = 1 and t.outApply = 0 and t.isApproval = 0")
    List<ProductModel> findNoApplyModel(String pid,String color);

}
