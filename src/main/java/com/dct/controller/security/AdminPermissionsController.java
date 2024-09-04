package com.dct.controller.security;

import com.dct.model.dct.AdminPermissionsModel;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.security.IAdminPermissionsService;
import com.dct.utils.ResponseInfoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 * @author ladder
 */
@Slf4j
@RestController
@RequestMapping("/admin/permission")
public class AdminPermissionsController {

    @Autowired
    IAdminPermissionsService adminPermissionsService;

    @GetMapping(value = "/list")
    @ApiOperation(value = "权限列表", notes = "获取所有权限")
    public ResponseInfoVO findAll(){
        return ResponseInfoUtil.success(adminPermissionsService.findAllPermission());
    }

    @GetMapping(value = "/get/{id}")
    @ApiOperation(value = "权限获取", notes = "获取权限")
    public ResponseInfoVO findOne(@PathVariable String id){
        return ResponseInfoUtil.success(adminPermissionsService.findById(id));
    }

    @PostMapping(value = "/save")
    @ApiOperation(value = "保存权限", notes = "保存权限")
    public ResponseInfoVO saveRole(@RequestBody AdminPermissionsModel adminPermissionsModel){
        if(adminPermissionsModel != null){
            adminPermissionsService.save(adminPermissionsModel);
        }
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除权限", notes = "删除权限")
    public ResponseInfoVO saveRole(@PathVariable String id){
        adminPermissionsService.delete(id);
        return ResponseInfoUtil.success();
    }

}
