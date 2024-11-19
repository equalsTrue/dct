package com.dct.service.account;

import com.alibaba.fastjson.JSONObject;
import com.dct.model.dct.AccountLogModel;
import com.dct.model.dct.AccountModel;
import com.dct.model.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 16:22
 */
public interface IAccountService {
    /**
     * 导入账号文件.
     */
    void importAccountFile(List<MultipartFile> accountFiles, String manager);

   PageVO fetchAccountList(JSONObject params);

   PageVO fetchExportAccountList(JSONObject params);

    void updateAccount(JSONObject params);

    /**
     * 查看日志.
     * @param params
     * @return
     */
    List<AccountLogModel> fetchAccountLog(JSONObject params);

    /**
     * 分配账号
     * @param params
     */
    void updateAssignAccount(JSONObject params);

    /**
     * 查询参数.
     * @return
     */
    JSONObject fetchAccountParam();

    /**
     * 删除账号.
     * @param uid
     * @return
     */
    String deleteAccount(String uid);


    /**
     * 保存账号.
     * @param params
     * @return
     */
    String saveAccount(JSONObject params);

    AccountModel fetchAccountModel(String id);

}
