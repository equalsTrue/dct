package com.dct.controller.account;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 16:21
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.account.IAccountService;
import com.dct.utils.ResponseInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 账号管理
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 16:21 
 */
@RestController
@RequestMapping("/dct/account")
public class AccountManagerController {

    @Autowired
    private IAccountService accountService;

    /**
     * 导入账号文件.
     * @return
     */
    @PostMapping("/import")
    public ResponseInfoVO importAccount(@RequestParam List<MultipartFile> accountFiles){
        accountService.importAccountFile(accountFiles);
        return ResponseInfoUtil.success();
    }

    /**
     * 保存账号.
     * @param params
     * @return
     */
    @PostMapping("/save")
    public ResponseInfoVO saveAccount(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(accountService.saveAccount(params));
    }


    /**
     * 查询.
     * @param params
     * @return
     */
    @PostMapping("/list")
    public ResponseInfoVO accountList(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(accountService.fetchAccountList(params));
    }

    /**
     * 分配账号.
     * @param params
     * @return
     */
    @PostMapping("/assign")
    public ResponseInfoVO assignAccount(@RequestBody JSONObject params){
        accountService.updateAssignAccount(params);
        return ResponseInfoUtil.success();
    }

    /**
     * 更新账号.
     * @param params
     * @return
     */
    @PostMapping("/update")
    public ResponseInfoVO updateNewAccount(@RequestBody JSONObject params){
        accountService.updateAccount(params);
        return ResponseInfoUtil.success();
    }

    /**
     * 查询日志.
     * @param params
     * @return
     */
    @PostMapping("/log")
    public ResponseInfoVO fetchHandleAccountLog(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(accountService.fetchAccountLog(params));
    }

    /**
     * 展示参数.
     * @return
     */
    @GetMapping("/params")
    public ResponseInfoVO fetchAccountParams(){
        return ResponseInfoUtil.success(accountService.fetchAccountParam());
    }


    /**
     * 删除账号.
     * @param uid
     * @return
     */
    @GetMapping("/delete")
    public ResponseInfoVO deleteAccount(@RequestParam String uid){
        return ResponseInfoUtil.success(accountService.deleteAccount(uid));
    }





}
