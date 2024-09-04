package com.dct.repo.security;

import com.dct.model.dct.AdminPermissionsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author      ：Vic.
 * @ Date       ：Created in 5:46 PM 2019/1/7
 * @ Description：${description}
 * @ Modified By：
 * @Version: $version$
 */
public interface AdminPermissionsRepo extends JpaRepository<AdminPermissionsModel, String>, JpaSpecificationExecutor<AdminPermissionsModel> {

    /**
     * 获取父节点
     * @param id
     * @return
     */
    List<AdminPermissionsModel> findByParentId(String id);


    /**
     * 根据ID获取路径.
     * @param id
     * @return
     */
    @Query("select p.path from AdminPermissionsModel p where p.id = ?1")
    String findPathById(String id);

    /**
     * 找到子权限
     * @param id
     * @return
     */
    @Query("select t from AdminPermissionsModel t where t.parentId=?1 order by t.sortIndex asc, t.name asc")
    List<AdminPermissionsModel> findChildren(String id);

    /**
     * 根据level 查询
     * @param level
     * @return
     */
    List<AdminPermissionsModel> findAllByLevelOrderBySortIndexAsc(int level);

    /**
     * 修改父权限是否为叶节点（isLeaf） 属性
     * @param isLeaf
     * @param id
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update AdminPermissionsModel t set t.isLeaf=?1 where t.id=?2")
    void updateLeaf(boolean isLeaf, String id);

    /**
     * 查询目录的数量
     * @param level
     * @return
     */
    Long countByLevel(int level);

    /**
     * 查询当前父节点下权限数量
     * @param parentId
     * @return
     */
    Long countByParentId(String parentId);

    /**
     * 是否存在子权限
     * @param id
     * @return
     */
    boolean existsByParentId(String id);


    /**
     * 查找父级权限
     * @param parentId
     * @return
     */
    AdminPermissionsModel findFirstByParentId(String parentId);
}
