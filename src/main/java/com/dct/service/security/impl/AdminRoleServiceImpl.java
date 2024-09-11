package com.dct.service.security.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dct.constant.consist.MainConstant;

import com.dct.model.dct.AdminPermissionsModel;
import com.dct.model.dct.AdminRoleModel;
import com.dct.model.dct.AdminRolePermissionsModel;
import com.dct.repo.security.AdminPermissionsRepo;
import com.dct.repo.security.AdminRolePermissionsRepo;
import com.dct.repo.security.AdminRoleRepo;
import com.dct.repo.security.AdminUserRoleRepo;
import com.dct.security.JwtUtils;
import com.dct.service.security.IAdminRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Charles
 * @date 2019/2/12
 * @description :
 */
@Slf4j
@Service
public class AdminRoleServiceImpl implements IAdminRoleService {

    @Autowired
    private AdminRoleRepo adminRoleRepo;

    @Autowired
    private AdminUserRoleRepo adminUserRoleRepo;

    @Autowired
    private AdminRolePermissionsRepo adminRolePermissionsRepo;

    @Autowired
    private AdminPermissionsRepo adminPermissionsRepo;

    @PersistenceContext
    EntityManager seEntityManager;

    @Autowired
    private JwtUtils jwtUtils;



    @Override
    public List<AdminRoleModel> findAllRole(String token) {
        //如果当前用户没有角色管理权限，则不返回有角色管理的角色
        String username = jwtUtils.getUsernameFromToken(token);
        StringBuilder checkRoleSql = new StringBuilder();
        checkRoleSql.append("SELECT count(1) ")
                .append("FROM admin_user u, admin_user_role ur, admin_role r, admin_role_permissions rp, admin_permissions p ")
                .append("WHERE u.`id` = ur.`user_id` AND ur.role_id=r.id AND r.`id`=rp.role_id AND rp.`permissions_id` = p.id ")
                .append("AND u.userName = :username AND p.path='role'");
        Query nativeQuery = seEntityManager.createNativeQuery(checkRoleSql.toString());
        nativeQuery.setParameter("username", username);
        nativeQuery.unwrap(NativeQuery.class);
        int count = ((BigInteger) nativeQuery.getResultList().get(0)).intValue();
        if (count > 0) {
            List<AdminRoleModel> adminRoleModelList = adminRoleRepo.findAll();
            return adminRoleModelList;
        } else {
            String sql = "SELECT r.id " +
                    "FROM admin_role r, admin_role_permissions rp, admin_permissions p " +
                    "WHERE r.`id`=rp.role_id AND rp.`permissions_id` = p.id " +
                    "AND p.path='role'";
            Query nQuery = seEntityManager.createNativeQuery(sql);
            nQuery.unwrap(NativeQuery.class);
            List<String> roleIds = nQuery.getResultList();
            if (roleIds != null && roleIds.size() > 0) {
                List<AdminRoleModel> roleModels = adminRoleRepo.findAll();
                List<AdminRoleModel> list = new ArrayList<>();
                if (roleModels != null && roleModels.size() > 0) {
                    for (AdminRoleModel roleModel : roleModels) {
                        if (!roleIds.contains(roleModel.getId())) {
                            list.add(roleModel);
                        }
                    }
                    return list;
                }
            }
        }
        return null;
    }



    @Override
    public AdminRoleModel findById(String id) {
        return adminRoleRepo.findById(id).get();
    }

    @Override
    public void save(JSONObject jsonObject) {
        AdminRoleModel adminRoleModel = JSON.parseObject(JSON.toJSONString(jsonObject.get(MainConstant.INFO)), AdminRoleModel.class);
        if (adminRoleModel != null) {
            AdminRoleModel saveModel = adminRoleRepo.save(adminRoleModel);
            JSONArray jsonArray = jsonObject.getJSONArray(MainConstant.CHOSEN);
            if (jsonArray != null) {
                List<String> initChosen = jsonArray.toJavaList(String.class);
                //找到权限及权限的父级
                List<String> choson = getAllChosenWithParent(initChosen);
                List<AdminRolePermissionsModel> rolePermissionsModels = adminRolePermissionsRepo.findAllByRoleId(String.valueOf(saveModel.getId()));
                if (rolePermissionsModels != null && rolePermissionsModels.size() > 0) {
                    for (AdminRolePermissionsModel adminRolePermissionsModel : rolePermissionsModels) {
                        if (!choson.contains(adminRolePermissionsModel.getPermissionsId())) {
                            adminRolePermissionsRepo.delete(adminRolePermissionsModel);
                        }
                    }
                }
                for (String permissionId : choson) {
                    if (!adminRolePermissionsRepo.existsByRoleIdAndAndPermissionsId(saveModel.getId(), permissionId)) {
                        AdminRolePermissionsModel adminRolePermissionsModel = new AdminRolePermissionsModel();
                        adminRolePermissionsModel.setRoleId(String.valueOf(saveModel.getId()));
                        adminRolePermissionsModel.setPermissionsId(permissionId);
                        adminRolePermissionsRepo.save(adminRolePermissionsModel);
                    }
                }
            }
        }
    }

    private List<String> getAllChosenWithParent(List<String> initChosen) {
        List<String> result = new ArrayList<>();
        result.addAll(initChosen);
        //找到这个权限的所有祖先权限
        for (String permissionId : initChosen) {
            List<AdminPermissionsModel> progenitors = new ArrayList<>();
            AdminPermissionsModel child = adminPermissionsRepo.findById(permissionId).get();
            getProgenitors(child, progenitors);
            if (progenitors != null && progenitors.size() > 0) {
                for (AdminPermissionsModel adminPermissionsModel : progenitors) {
                    result.add(String.valueOf(adminPermissionsModel.getId()));
                }
            }
        }
        return result;
    }

    /**
     * 查找所有子节点跟父节点，递归调用.
     * @param child
     * @param progenitors
     */
    private void getProgenitors(AdminPermissionsModel child, List<AdminPermissionsModel> progenitors) {
        if (child != null && progenitors != null && StringUtils.isNotBlank(child.getParentId())) {
            AdminPermissionsModel parent = adminPermissionsRepo.findById(child.getParentId()).get();
            progenitors.add(parent);
            getProgenitors(parent, progenitors);
        }
    }

    @Override
    public void delete(String id) {
        adminRoleRepo.deleteById(id);
        //删除角色关联
        adminUserRoleRepo.deleteByRoleId(id);
        // 删除权限关联
        adminRolePermissionsRepo.deleteByRoleId(id);
    }

    @Override
    public List<String> getEnablePermission(String id) {
        List<String> enableIds = new ArrayList<>();
        List<AdminRolePermissionsModel> rolePermissionsModels = adminRolePermissionsRepo.findAllByRoleId(id);
        List<AdminPermissionsModel> permissionsModels = adminPermissionsRepo.findAll();
        if (rolePermissionsModels != null && rolePermissionsModels.size() > 0) {
            for (AdminRolePermissionsModel adminRolePermissionsModel : rolePermissionsModels) {
                if (permissionsModels != null && permissionsModels.size() > 0) {
                    for (AdminPermissionsModel permissionsModel : permissionsModels) {
                        if (String.valueOf(permissionsModel.getId()).equalsIgnoreCase(adminRolePermissionsModel.getPermissionsId())) {
                            if (permissionsModel.isLeaf()) {
                                enableIds.add(adminRolePermissionsModel.getPermissionsId());
                            }
                        }
                    }
                }
            }
        }
        return enableIds;
    }

    @Override
    public List<String> queryRoleAppInfo(JSONObject params) {
        List<String> appNameList = new ArrayList<>();
        JSONArray roleArray = params.getJSONArray("role");
        if(roleArray != null && roleArray.size() >0){
            List<String> roleNameList = JSONArray.parseArray(JSON.toJSONString(roleArray),String.class);
            List<String> roleIdList = adminRoleRepo.findRoleIds(roleNameList);
        }
        return appNameList;
    }


}
