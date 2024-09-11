package com.dct.service.security.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dct.constant.consist.MainConstant;
import com.dct.exception.CustomException;
import com.dct.model.dct.AdminConfigMapModel;
import com.dct.model.dct.AdminRoleModel;
import com.dct.model.dct.AdminUserModel;
import com.dct.model.dct.AdminUserRoleModel;
import com.dct.model.vo.AdminUserVO;
import com.dct.model.vo.OptionVO;
import com.dct.repo.security.AdminConfigMapRepo;
import com.dct.repo.security.AdminRoleRepo;
import com.dct.repo.security.AdminUserRepo;
import com.dct.repo.security.AdminUserRoleRepo;
import com.dct.security.JwtUtils;
import com.dct.security.PasswordEncoder;
import com.dct.security.ResultCode;
import com.dct.service.security.IAdminRoleService;
import com.dct.service.security.IAdminUserService;
import com.dct.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Vic on 2019/1/16
 */
@Service
public class AdminUserServiceImpl implements IAdminUserService {

    @Autowired
    AdminUserRepo adminUserRepo;

    @Autowired
    AdminRoleRepo adminRoleRepo;

    @Autowired
    AdminUserRoleRepo adminUserRoleRepo;

    @Autowired
    AdminConfigMapRepo adminConfigMapRepo;

    @Autowired
    private IAdminRoleService adminRoleService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public List<AdminUserVO> findAllUser(String token) {
        List<AdminUserVO> userList = new ArrayList<>();
        //如果不拥有高级权限，则无法查看高级权限的用户
        //获取可修改的角色列表
        List<AdminRoleModel> adminRoleModels = adminRoleService.findAllRole(token);
        if(adminRoleModels != null && adminRoleModels.size() > 0){
            List<String> roleIds = new ArrayList<>();
            for(AdminRoleModel adminRoleModel : adminRoleModels){
                roleIds.add(adminRoleModel.getId());
            }
            String sql = "SELECT a.id, a.createTime, a.username, a.email, a.work_we_chat_user_id, GROUP_CONCAT(r.`roleName` SEPARATOR '|') AS roles " +
                    "FROM ( " +
                    "SELECT u.id, u.createtime, u.username, u.email, u.work_we_chat_user_id, ur.role_id " +
                    "FROM admin_user u LEFT JOIN  admin_user_role ur " +
                    "ON ur.user_id= u.id " +
                    "WHERE ur.role_id IN (:roleIds) OR ur.role_id IS NULL OR ur.role_id=''" +
                    ") AS a " +
                    "LEFT JOIN admin_role r ON a.role_id = r.id " +
                    "GROUP BY a.id ORDER BY a.username ASC";
            Query nativeQuery = entityManager.createNativeQuery(sql);
            nativeQuery.unwrap(NativeQuery.class);
            nativeQuery.setParameter("roleIds", roleIds);
            List<Object[]> resultList = nativeQuery.getResultList();
            for (Object[] result : resultList) {
                AdminUserVO adminUserVO = new AdminUserVO();
                adminUserVO.setId(String.valueOf(result[0]));
                adminUserVO.setCreateTime(Timestamp.valueOf(String.valueOf(result[1] != null ? result[1] : DateUtil.formatYyyyMmDdHhMmSs(new Date()))));
                adminUserVO.setUsername(String.valueOf(result[2]));
                adminUserVO.setEmail(String.valueOf(result[3]));
                adminUserVO.setWorkWeChatUserId(String.valueOf(result[4]));
                if(result[5] != null) {
                    String[] roles = Objects.requireNonNull(String.valueOf(result[5]).split(MainConstant.VERTICAL_LINE_PAT));
                    adminUserVO.setRoles(Arrays.asList(roles));
                }
                userList.add(adminUserVO);
            }
        }
        return userList;
    }


    @Override
    public void save(AdminUserVO adminUserVO) {
        if(null == adminUserVO || StringUtils.isBlank(adminUserVO.getUsername())
            || StringUtils.isBlank(adminUserVO.getUsername())
            || StringUtils.isBlank(adminUserVO.getPassword())
            || StringUtils.isBlank(adminUserVO.getEmail())) {
            throw new CustomException(ResultCode.LOGIN_ERROR.getCode(), "FormData cant be NULL.");
        }
        AdminUserModel firstByUsername = adminUserRepo.findFirstByUsername(adminUserVO.getUsername());
        if(null != firstByUsername){
            throw new CustomException(ResultCode.LOGIN_ERROR.getCode(), "Duplicate Username.");
        }
        AdminUserModel adminUserModel = new AdminUserModel();
        adminUserModel.setUsername(adminUserVO.getUsername());
        adminUserModel.setPassword(passwordEncoder.passwordEncoder().encode(adminUserVO.getPassword()));
        adminUserModel.setEmail(adminUserVO.getEmail());
        adminUserModel.setId(adminUserVO.getId());
        adminUserRepo.save(adminUserModel);
    }

    @Override
    public AdminUserVO getById(String id) {
        AdminUserModel adminUserModel = adminUserRepo.findById(id).get();
        AdminUserVO adminUserVO = new AdminUserVO();
        adminUserVO.setId(String.valueOf(adminUserModel.getId()));
        adminUserVO.setUsername(adminUserModel.getUsername());
        adminUserVO.setPassword("");
        adminUserVO.setConfirmPassword("");
        adminUserVO.setEmail(adminUserModel.getEmail());
        return adminUserVO;
    }

    @Override
    public AdminUserVO getEmail(String id) {
        AdminUserModel adminUserModel = adminUserRepo.findById(id).get();
        AdminUserVO adminUserVO = new AdminUserVO();
        adminUserVO.setId(String.valueOf(adminUserModel.getId()));
        adminUserVO.setEmail(adminUserModel.getEmail());
        return adminUserVO;
    }

    @Override
    public void resetPassword(AdminUserVO adminUserVO) {
        AdminUserModel adminUserModel = adminUserRepo.findById(adminUserVO.getId()).get();
        String newPassword = passwordEncoder.passwordEncoder().encode(adminUserVO.getPassword());
        String oldPassword = adminUserModel.getPassword();
        if(!passwordEncoder.passwordEncoder().matches(adminUserVO.getOldPassword(), oldPassword)) {
            throw new CustomException(ResultCode.LOGIN_ERROR.getCode(), "原密码不正确.");
        }
        if(passwordEncoder.passwordEncoder().matches(adminUserVO.getPassword(), oldPassword)){
            throw new CustomException(ResultCode.LOGIN_ERROR.getCode(), "新密码与旧密码不能相同.");
        }
        adminUserRepo.resetPassword(newPassword, adminUserVO.getId());
    }

    @Override
    public void deleteById(String id) {
        //删除用户
        adminUserRepo.deleteById(id);
        //删除角色关联
        adminUserRoleRepo.deleteByUserId(id);
    }

    @Override
    public void updateEmail(String id, String email) {
        adminUserRepo.updateEmail(email, id);
    }

    @Override
    public AdminUserVO findUser(String token) {
        String username = jwtUtils.getUsernameFromToken(token);
        String sql = "SELECT a.id, a.createtime, a.userName, a.email, a.work_we_chat_user_id, GROUP_CONCAT(r.`roleName` SEPARATOR '|') AS roles " +
                "FROM ( " +
                "SELECT u.id, u.createtime, u.username, u.email, u.work_we_chat_user_id, ur.role_id " +
                "FROM admin_user u LEFT JOIN  admin_user_role ur " +
                "ON ur.user_id= u.id " +
                "WHERE  u.userName=:username  " +
                ") AS a " +
                "LEFT JOIN admin_role r ON a.role_id = r.id " +
                "GROUP BY a.id";
        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter("username", username);
        List<Object[]> resultList = nativeQuery.getResultList();
        Object[] defaultResult = resultList.get(0);
        AdminUserVO adminUserVO = new AdminUserVO();
        adminUserVO.setId(String.valueOf(defaultResult[0]));
        adminUserVO.setCreateTime(Timestamp.valueOf(String.valueOf(defaultResult[1] != null ? defaultResult[1] : DateUtil.formatYyyyMmDdHhMmSs(new Date()))));
        adminUserVO.setUsername(String.valueOf(defaultResult[2]));
        adminUserVO.setEmail(String.valueOf(defaultResult[3]));
        adminUserVO.setWorkWeChatUserId(String.valueOf(defaultResult[4]));
        Object rolesObj = defaultResult[5];
        if(rolesObj != null) {
            String[] roles = Objects.requireNonNull(String.valueOf(defaultResult[4]).split(MainConstant.VERTICAL_LINE_PAT));
            adminUserVO.setRoles(Arrays.asList(roles));
        }
        return adminUserVO;
    }

    @Override
    public List<String> getRoleIds(String id) {
        List<AdminUserRoleModel> adminUserRoleModels = Objects.requireNonNull(adminUserRoleRepo.findAllByUserId(id));
        List<String> roleIds = new ArrayList<>();
        if(adminUserRoleModels != null && adminUserRoleModels.size() > 0) {
            for (AdminUserRoleModel userRoleModel : adminUserRoleModels) {
                roleIds.add(userRoleModel.getRoleId());
            }
        }
        return roleIds;
    }

    @Override
    public void updateRoles(JSONObject jsonObject) {
        if(jsonObject != null){
            String  userId = jsonObject.getString(MainConstant.ID);
            JSONArray roles = jsonObject.getJSONArray(MainConstant.ROLE);
            if(!StringUtils.isEmpty(userId)){
                // 获取以前的数据
                List<AdminUserRoleModel> adminUserRoleModels = Objects.requireNonNull(adminUserRoleRepo.findAllByUserId(userId));
                List<String> roleIds = roles.toJavaList(String.class);
                // 删除角色
                if(adminUserRoleModels != null && adminUserRoleModels.size() > 0) {
                    for(AdminUserRoleModel adminUserRoleModel : adminUserRoleModels){
                        if(roleIds != null && !roleIds.contains(adminUserRoleModel.getRoleId())){
                            adminUserRoleRepo.deleteById(adminUserRoleModel.getId());
                        }
                    }
                }
                List<String> originRoleIds = new ArrayList<>();
                if(adminUserRoleModels.size() > 0) {
                    for (AdminUserRoleModel userRoleModel : adminUserRoleModels) {
                        originRoleIds.add(userRoleModel.getRoleId());
                    }
                }
                // 添加角色
                if(roleIds != null && roleIds.size() > 0) {
                    for (String roleId : roleIds){
                        if(originRoleIds != null && originRoleIds.contains(roleId) && adminUserRoleRepo.existsByUserIdAndRoleId(userId, roleId)){
                            continue;
                        }
                        AdminUserRoleModel userRoleModel = new AdminUserRoleModel();
                        userRoleModel.setRoleId(roleId);
                        userRoleModel.setUserId(userId);
                        adminUserRoleRepo.save(userRoleModel);
                    }
                }
            }
        }
    }

    @Override
    public void saveChannelUser(AdminUserVO adminUserVO) {
        if(null == adminUserVO || StringUtils.isEmpty(adminUserVO.getUsername())
                || StringUtils.isEmpty(adminUserVO.getUsername())
                || StringUtils.isEmpty(adminUserVO.getPassword())
                || StringUtils.isEmpty(adminUserVO.getEmail())) {
            throw new CustomException(ResultCode.LOGIN_ERROR.getCode(), "FormData cant be NULL.");
        }
        AdminUserModel adminUserModel = new AdminUserModel();
        adminUserModel.setUsername(adminUserVO.getUsername());
        if(adminUserVO.getId()!=null&&adminUserVO.getId().length()>0){
            if(!adminUserVO.getPassword().equalsIgnoreCase(MainConstant.PASSWORD)){
                adminUserRepo.resetPassword(passwordEncoder.passwordEncoder().encode(adminUserVO.getPassword()),adminUserVO.getId());
            }
            adminUserRepo.updateEmail(adminUserVO.getEmail(),adminUserVO.getId());
        } else {
            adminUserModel.setPassword(passwordEncoder.passwordEncoder().encode(adminUserVO.getPassword()));
            adminUserModel.setEmail(adminUserVO.getEmail());
            adminUserModel.setId(adminUserVO.getId());
            adminUserRepo.save(adminUserModel);
        }
    }

    @Override
    public AdminUserModel findUserByUsername(String username) {
        return adminUserRepo.findUserByUsername(username);
    }

    @Override
    public String checkMail(String mail) {
        String beReturn = "0";
        List<String> list=adminUserRepo.checkMail(mail);
        if(list!=null&&list.size()>0){
            beReturn="1";
        }
        return beReturn;
    }

    @Override
    public AdminUserModel getUserByWorkWeChatUserId(String userId) {
        return adminUserRepo.findFirstByEmail(userId);
    }

    @Override
    public void resetWorkWeChatUserId(String token, String userId) {
        String username = jwtUtils.getUsernameFromToken(token);
        AdminUserModel userModel = adminUserRepo.findFirstByUsername(username);
        userModel.setWorkWeChatUserId(userId);
        adminUserRepo.save(userModel);
    }

    @Override
    public List<OptionVO> getAllEmails() {
        List<AdminUserModel> all = adminUserRepo.findAll();
        ArrayList<OptionVO> optionVOs = new ArrayList<>();
        for (AdminUserModel adminUserModel : all) {
            OptionVO optionVO = new OptionVO();
            optionVO.setLabel(adminUserModel.getUsername());
            optionVO.setValue(adminUserModel.getEmail());
            optionVO.setId(String.valueOf(adminUserModel.getId()));
            optionVOs.add(optionVO);
        }
        return optionVOs;
    }

    @Override
    public List<AdminConfigMapModel>  getConfigMap(String configName) {
        return adminConfigMapRepo.findAllByConfigNameOrderByItemKeyAsc(configName);
    }
}
