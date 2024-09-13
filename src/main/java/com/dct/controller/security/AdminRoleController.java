package com.dct.controller.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dct.common.constant.consist.MainConstant;
import com.dct.model.dct.AdminRoleModel;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.security.IAdminRoleService;
import com.dct.utils.ResponseInfoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Charles
 * @date 2019/2/12
 * @description :
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {

    @Autowired
    IAdminRoleService adminRoleService;

    @Value("${jwt.header}")
    private String tokenHeader;

    @GetMapping(value = "/list")
    @ApiOperation(value = "角色列表", notes = "获取所有角色")
    public ResponseInfoVO findAll(HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        return ResponseInfoUtil.success(adminRoleService.findAllRole(token));
    }

    @GetMapping(value = "/get/{id}")
    @ApiOperation(value = "角色获取", notes = "获取角色")
    public ResponseInfoVO findOne(@PathVariable String id){
        AdminRoleModel adminRoleModel = adminRoleService.findById(id);
        JSONObject jsonObject = null;
        if(adminRoleModel != null) {
            jsonObject = new JSONObject();
            jsonObject.put(MainConstant.INFO, JSON.toJSON(adminRoleModel));
            jsonObject.put(MainConstant.CHOSEN, adminRoleService.getEnablePermission(id));
        }
        return ResponseInfoUtil.success(jsonObject);
    }

    @PostMapping(value = "/save")
    @ApiOperation(value = "保存角色", notes = "保存角色")
    public ResponseInfoVO saveRole(@RequestBody JSONObject jsonObject){
        if(jsonObject != null){
            adminRoleService.save(jsonObject);
        }
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除角色", notes = "删除角色")
    public ResponseInfoVO saveRole(@PathVariable String id){
        adminRoleService.delete(id);
        return ResponseInfoUtil.success();
    }

    /**
     * 根据权限查询分配的App.
     * @param params
     * @return
     */
    @PostMapping(value = "/query/app")
    public List<String> queryAppInfo(@RequestBody JSONObject params){
        return adminRoleService.queryRoleAppInfo(params);
    }



}
