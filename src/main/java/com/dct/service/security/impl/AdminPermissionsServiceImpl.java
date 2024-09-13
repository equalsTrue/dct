package com.dct.service.security.impl;

import com.dct.common.constant.consist.MainConstant;
import com.dct.exception.CustomException;
import com.dct.model.dct.AdminPermissionsModel;
import com.dct.model.dct.AdminRolePermissionsModel;
import com.dct.model.dct.AdminUserModel;
import com.dct.model.vo.PermissionVO;
import com.dct.model.vo.ViewRouterMetaVO;
import com.dct.model.vo.ViewRouterVO;
import com.dct.repo.security.AdminPermissionsRepo;
import com.dct.repo.security.AdminRolePermissionsRepo;
import com.dct.repo.security.AdminRoleRepo;
import com.dct.security.JwtUtils;
import com.dct.security.ResultCode;
import com.dct.security.UserDetail;
import com.dct.service.security.IAdminPermissionsService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author starp
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AdminPermissionsServiceImpl implements IAdminPermissionsService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AdminPermissionsRepo adminPermissionsRepo;

    @Autowired
    AdminRolePermissionsRepo adminRolePermissionsRepo;

    @Autowired
    private AdminRoleRepo adminRoleRepo;

    @Autowired
    public AdminPermissionsServiceImpl(AuthenticationManager authenticationManager, @Qualifier("CustomUserDetailsService") UserDetailsService userDetailsService, JwtUtils jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public AdminPermissionsModel getPermissions(String permissionsId) {
        return adminPermissionsRepo.findById(permissionsId).get();
    }

    @Override
    public void removePermissions(String permissionsId) {
        adminPermissionsRepo.deleteById(permissionsId);
    }

    @Override
    public AdminPermissionsModel savePermissions(AdminPermissionsModel model) {
        return adminPermissionsRepo.save(model);
    }

    private Authentication authenticate(String username, String password) {
        try {
            //该方法会去调用userDetailsService.loadUserByUsername()去验证用户名和密码，如果正确，则存储该用户名密码到“security 的 context中”
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new CustomException(ResultCode.LOGIN_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public String login(String username, String password) {
        //用户验证
        final Authentication authentication = authenticate(username, password);
        //存储认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //生成token
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateAccessToken(userDetail);
        //存储token
        jwtTokenUtil.putToken(username, token);
        return token;
    }

    @Override
    public String login(AdminUserModel userModel) {
        final UserDetail userDetail = new UserDetail(String.valueOf(userModel.getId()), userModel.getUsername(), userModel.getPassword());
        final String token = jwtTokenUtil.generateAccessToken(userDetail);
        //存储token
        jwtTokenUtil.putToken(userModel.getUsername(), token);
        return token;
    }

    @Override
    public void logout(String token) {
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        jwtTokenUtil.deleteToken(userName);
    }

    @Override
    public List<AdminPermissionsModel> getPermissionsList(AdminPermissionsModel searchModel) {
        Specification specification = new Specification<AdminPermissionsModel>() {
            @Override
            public Predicate toPredicate(Root<AdminPermissionsModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                String parentId = searchModel.getParentId();
                if (StringUtils.isNotBlank(parentId)) {
                    predicates.add(criteriaBuilder.equal(root.get("parentId"), parentId));
                }
                String title = searchModel.getTitle();
                if (StringUtils.isNotBlank(title)) {
                    predicates.add(criteriaBuilder.equal(root.get("title"), title));
                }
                String path = searchModel.getPath();
                if (StringUtils.isNotBlank(path)) {
                    predicates.add(criteriaBuilder.equal(root.get("path"), path));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("level")), criteriaBuilder.asc(root.get("sortIndex")));
                return criteriaQuery.getRestriction();
            }
        };

        // 获取带有关键字的报警配置
        return adminPermissionsRepo.findAll(specification);
    }

    @Override
    public AdminPermissionsModel editPermissions(AdminPermissionsModel model) {
        String id = Objects.requireNonNull(model.getId());
        AdminPermissionsModel originalModel = Objects.requireNonNull(adminPermissionsRepo.findById(id).get());
        originalModel.setParentId(model.getParentId());
        originalModel.setTitle(model.getTitle());
        originalModel.setPath(model.getPath());
        originalModel.setSortIndex(model.getSortIndex());
        return adminPermissionsRepo.save(originalModel);
    }


    @Override
    public List<AdminPermissionsModel> getPermissionsListWithSub(String roleId) {
        return null;
    }


    @Override
    public void editRolePermission(String roleId, String[] permissionsId) {
        adminRolePermissionsRepo.deleteByRoleId(roleId);
        if (permissionsId == null) {
            return;
        }
        for (String id : permissionsId) {
            AdminRolePermissionsModel model = new AdminRolePermissionsModel();
            model.setRoleId(roleId);
            model.setPermissionsId(id);
            adminRolePermissionsRepo.save(model);
        }
    }

    @Override
    public List<AdminPermissionsModel> getParentPermissionsList() {
        return null;
//		return adminPermissionsRepo.findByType(PermissionsType.FUNCTION);
    }

    @Override
    public List<PermissionVO> findAllPermission() {
        List<AdminPermissionsModel> adminPermissionsModels = adminPermissionsRepo.findAllByLevelOrderBySortIndexAsc(0);
        List<PermissionVO> permissionVOS = new ArrayList<>();
        if (adminPermissionsModels != null && adminPermissionsModels.size() > 0) {
            for (AdminPermissionsModel adminPermissionsModel : adminPermissionsModels) {
                PermissionVO permissionVo = findPermissionWithChildren(adminPermissionsModel);
                if (permissionVo != null) {
                    permissionVOS.add(permissionVo);
                }
            }
        }
        return permissionVOS;
    }

    /**
     * 获取某个权限及其子权限
     *
     * @param permissionsModel
     * @return
     */
    public PermissionVO findPermissionWithChildren(AdminPermissionsModel permissionsModel) {
        PermissionVO permissionVo = null;
        if (permissionsModel != null) {
            permissionVo = new PermissionVO();
            permissionVo.setId(permissionsModel.getId());
            permissionVo.setLabel(permissionsModel.getName());
            permissionVo.setDisabled(false);
            permissionVo.setLeaf(permissionsModel.isLeaf());
            // 如果不是叶节点，则递归
            if (!permissionsModel.isLeaf()) {
                List<AdminPermissionsModel> childPermissions = adminPermissionsRepo.findChildren(permissionsModel.getId());
                List<PermissionVO> children = new ArrayList<>();
                if (childPermissions != null && childPermissions.size() > 0) {
                    for (AdminPermissionsModel childModel : childPermissions) {
                        PermissionVO childPermissionVO = findPermissionWithChildren(childModel);
                        if (childPermissionVO != null) {
                            children.add(childPermissionVO);
                        }
                    }
                }
                permissionVo.setChildren(children);
            }
        }
        return permissionVo;
    }

    @Override
    public AdminPermissionsModel findById(String id) {
        return adminPermissionsRepo.findById(id).get();
    }

    @Override
    public void save(AdminPermissionsModel adminPermissionsModel) {
        if ( StringUtils.isNotBlank(adminPermissionsModel.getParentId())) {
            // 判断有没有子权限
            if (StringUtils.isNotBlank(adminPermissionsModel.getId())) {
                Boolean isHaveSub = adminPermissionsRepo.existsByParentId(adminPermissionsModel.getId());
                if (isHaveSub != null && isHaveSub) {
                    adminPermissionsModel.setLeaf(false);
                }
            } else {
                adminPermissionsModel.setLeaf(true);
            }
            if (MainConstant.UNDEFINED.equals(adminPermissionsModel.getParentId()) || StringUtils.isBlank(adminPermissionsModel.getParentId())) {
                //这条权限为目录
                adminPermissionsModel.setParentId(null);
                adminPermissionsModel.setLevel(0);
                Long countCatalog = adminPermissionsRepo.countByLevel(0);
                adminPermissionsModel.setSortIndex(countCatalog != null ? countCatalog.intValue() : 0);
            } else {
                //设置父权限 isLeaf 为false
                adminPermissionsRepo.updateLeaf(false, adminPermissionsModel.getParentId());
                AdminPermissionsModel parent = adminPermissionsRepo.findById(adminPermissionsModel.getParentId()).get();
                adminPermissionsModel.setLevel(parent.getLevel() + 1);
                Long count = adminPermissionsRepo.countByParentId(adminPermissionsModel.getParentId());
                adminPermissionsModel.setSortIndex(count != null ? count.intValue() : 0);
            }
        }
        if (StringUtils.isEmpty(adminPermissionsModel.getRedirect())) {
            adminPermissionsModel.setRedirect(null);
        }
        if(StringUtils.isBlank(adminPermissionsModel.getPath())){
            adminPermissionsModel.setPermissionType(2);
        }else {
            adminPermissionsModel.setPermissionType(1);
        }
        adminPermissionsRepo.save(adminPermissionsModel);
    }

    @Override
    public void delete(String id) {
        AdminPermissionsModel origin = adminPermissionsRepo.findById(id).get();
        // 删除本权限
        adminPermissionsRepo.deleteById(id);
        //删除角色关联
        adminRolePermissionsRepo.deleteByPermissionsId(id);
        // 递归删除子权限
        List<AdminPermissionsModel> children = adminPermissionsRepo.findByParentId(id);
        if (children != null && children.size() > 0) {
            for (AdminPermissionsModel adminPermissionsModel : children) {
                delete(adminPermissionsModel.getId());
            }
        }
        //判断是否父权限只有这一个
        if (origin.getParentId() != null) {
            boolean isNode = adminPermissionsRepo.existsByParentId(origin.getParentId());
            if (!isNode) {
                adminPermissionsRepo.updateLeaf(true, origin.getParentId());
            }
        }
    }

    @Override
    public List<ViewRouterVO> getUserPermittedMenuList(String token) {

        String username = jwtUtils.getUsernameFromToken(token);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id,p.parent_id,p.title,p.path,p.sort_index,p.icon,p.name,p.hidden,p.level,p.redirect,p.is_leaf,p.operator ")
                .append("FROM admin_user u, admin_user_role ur, admin_role r, admin_role_permissions rp, admin_permissions p ")
                .append("where u.`id` = ur.`user_id` and ur.role_id=r.id and r.`id`=rp.role_id and rp.`permissions_id` = p.id and u.userName = :userName ")
                .append("GROUP BY p.id ORDER BY p.`level` asc, p.sort_index asc, parent_id asc");
        Query nativeQuery = entityManager.createNativeQuery(sql.toString());
        nativeQuery.setParameter("userName", username);
        nativeQuery.unwrap(NativeQuery.class);
        List<AdminPermissionsModel> allPermissions = new ArrayList<>();
        List<Object[]> resultList = nativeQuery.getResultList();
        for (Object[] result : resultList) {
            AdminPermissionsModel adminPermissionsModel = new AdminPermissionsModel();
            adminPermissionsModel.setId(String.valueOf(result[0]));
            adminPermissionsModel.setParentId(String.valueOf(result[1]));
            adminPermissionsModel.setTitle(String.valueOf(result[2]));
            adminPermissionsModel.setPath(String.valueOf(result[3]));
            adminPermissionsModel.setSortIndex(Integer.valueOf(String.valueOf(result[4])));
            adminPermissionsModel.setIcon(String.valueOf(result[5]));
            adminPermissionsModel.setName(String.valueOf(result[6]));
            adminPermissionsModel.setHidden(Boolean.valueOf(String.valueOf(result[7])));
            adminPermissionsModel.setLevel(Integer.valueOf(String.valueOf(result[8])));
            adminPermissionsModel.setRedirect((String) result[9]);
            adminPermissionsModel.setLeaf(Boolean.valueOf(String.valueOf(result[10])));
            adminPermissionsModel.setOperator(result[11] != null ? (String) result[11] : MainConstant.ALL);
            allPermissions.add(adminPermissionsModel);
        }

        List<AdminPermissionsModel> menuList = new ArrayList<>();
        for (AdminPermissionsModel permissionsModel : allPermissions) {
            if (permissionsModel.getLevel() == 0) {
                initSubPermissions(allPermissions, permissionsModel);
                menuList.add(permissionsModel);
            }

        }

        List<ViewRouterVO> viewRouterVOS = new ArrayList<>();
        for (AdminPermissionsModel adminPermissionsModel : menuList) {
            ViewRouterVO viewRouterVO = new ViewRouterVO();
            viewRouterVO.setComponent("");
            viewRouterVO.setOperatorPath(adminPermissionsModel.getOperatorPath());
            viewRouterVO.setOperator(adminPermissionsModel.getOperator());
            viewRouterVO.setLeaf(adminPermissionsModel.isLeaf());
            if (!StringUtils.isEmpty(adminPermissionsModel.getRedirect())) {
                viewRouterVO.setRedirect(adminPermissionsModel.getRedirect());
            }
            viewRouterVO.setOperatorPath(adminPermissionsModel.getPath());
            viewRouterVO.setHidden(adminPermissionsModel.isHidden());
            viewRouterVO.setName(adminPermissionsModel.getName());
            if (!adminPermissionsModel.isLeaf()) {
                ViewRouterMetaVO viewRouterMetaVO = new ViewRouterMetaVO();
                viewRouterMetaVO.setIcon(adminPermissionsModel.getIcon());
                viewRouterMetaVO.setTitle(adminPermissionsModel.getTitle());
                viewRouterVO.setMeta(viewRouterMetaVO);
                String routerPath = adminPermissionsModel.getPath();
                viewRouterVO.setPath(routerPath);
                initSubViewRouters(adminPermissionsModel.getSubPermissions(), viewRouterVO);
            } else {
                viewRouterVO.setPath("");
                ViewRouterVO child = new ViewRouterVO();
                child.setPath(adminPermissionsModel.getPath());
                child.setLeaf(true);
                child.setName(adminPermissionsModel.getName());
                ViewRouterMetaVO childMetaVO = new ViewRouterMetaVO();
                childMetaVO.setTitle(adminPermissionsModel.getTitle());
                childMetaVO.setIcon(adminPermissionsModel.getIcon());
                child.setMeta(childMetaVO);
                List<ViewRouterVO> children = new ArrayList<>();
                children.add(child);
                viewRouterVO.setChildren(children);
            }
            viewRouterVOS.add(viewRouterVO);
        }
        return viewRouterVOS;
    }

    @Override
    public List<String> getRoleNames(String token) {
        String username = jwtUtils.getUsernameFromToken(token);
        List<String> roleNames = adminRoleRepo.queryRoleNames(username);
        return roleNames;
    }

    @Override
    public String getUserName(String token) {
        return jwtUtils.getUsernameFromToken(token);
    }

    private void initSubViewRouters(List<AdminPermissionsModel> subPermissions, ViewRouterVO parentRouter) {
        if (null == subPermissions || null == parentRouter || subPermissions.size() == 0) {
            return;
        }
        ArrayList<ViewRouterVO> children = new ArrayList<>();
        for (AdminPermissionsModel permission : subPermissions) {
            ViewRouterVO child = new ViewRouterVO();
            String childPath = permission.getPath();
            child.setPath(childPath);
            child.setComponent("");
            child.setHidden(permission.isHidden());
            child.setLeaf(permission.isLeaf());
            ViewRouterMetaVO viewRouterMetaVO = new ViewRouterMetaVO();
            viewRouterMetaVO.setIcon(permission.getIcon());
            viewRouterMetaVO.setTitle(permission.getTitle());
            child.setMeta(viewRouterMetaVO);
            child.setName(permission.getName());
            child.setOperator(permission.getOperator());
            if (!StringUtils.isEmpty(permission.getRedirect())) {
                child.setRedirect(permission.getRedirect());
            }
            children.add(child);
            initSubViewRouters(permission.getSubPermissions(), child);

        }
        ArrayList<ViewRouterVO> filterChild = new ArrayList<>();
        StringBuffer operatorStr = new StringBuffer();
        if(children != null && children.size() >0){
            children.stream().forEach(a->{
                if(!operatorStr.toString().contains(a.getOperator())){
                    operatorStr.append(a.getOperator());
                    if(children.indexOf(a) != (children.size() -1)){
                        operatorStr.append(",");
                    }
                }
                if(StringUtils.isNotBlank(a.getPath())){
                    filterChild.add(a);
                }
            });
        }
        parentRouter.setOperator(operatorStr.toString());
        boolean operatorLeaf = checkOperatorLeaf(subPermissions);
        if(operatorLeaf){
            parentRouter.setLeaf(true);
        }
        if(filterChild.size() >0){
            parentRouter.setChildren(filterChild);
        }else {
            parentRouter.setChildren(null);
        }
    }

    private boolean checkOperatorLeaf(List<AdminPermissionsModel> subPermissions) {
        boolean leaf = true;
        if(subPermissions != null && subPermissions.size() >0){
           for (AdminPermissionsModel adminPermissionsModel : subPermissions){
               String operator = adminPermissionsModel.getOperator();
               String path = adminPermissionsModel.getPath();
               if(operator.equals(MainConstant.ALL) || StringUtils.isNotBlank(path)){
                   leaf = false;
                   break;
               }
           }
        }
        return leaf;
    }

    private void initSubPermissions(List<AdminPermissionsModel> allPermissions, AdminPermissionsModel parentPermission) {
        if (null != parentPermission) {
            List<AdminPermissionsModel> subList = new ArrayList<>();
            for (AdminPermissionsModel permissionModel : allPermissions) {
                if (parentPermission.getId().equals(permissionModel.getParentId())) {
                    if (!permissionModel.isLeaf() && permissionModel.getLevel() > 0) {
                        initSubPermissions(allPermissions, permissionModel);
                    }
                    subList.add(permissionModel);
                }
            }
            parentPermission.setSubPermissions(subList);
        }
    }
}
