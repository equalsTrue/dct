package com.dct.service.account.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 16:23
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dct.model.dct.AccountLogModel;
import com.dct.model.dct.AccountModel;
import com.dct.model.vo.AccountVo;
import com.dct.model.vo.AdminUserVO;
import com.dct.repo.account.AccountLogRepo;
import com.dct.repo.account.AccountRepo;
import com.dct.repo.security.AdminRoleRepo;
import com.dct.repo.security.AdminUserRepo;
import com.dct.repo.security.AdminUserRoleRepo;
import com.dct.service.account.IAccountService;
import com.dct.service.security.IAdminUserService;
import com.dct.utils.DateUtil;
import com.dct.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 16:23 
 */
@Service
@Slf4j
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IAdminUserService userService;

    @Autowired
    private AccountRepo accountRepo;


    @Autowired
    private AccountLogRepo accountLogRepo;

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private AdminRoleRepo roleRepo;

    @Override
    @Async
    public void importAccountFile(List<MultipartFile> files) {
        try {
            List<AccountModel> allAccountList = new ArrayList<>();
            files.stream().forEach(a->{
                File tempFile = FileUtil.multipartFileToFile(a);
                List<AccountModel> accountModelList = handleImportAccountTask(tempFile);
                allAccountList.addAll(accountModelList);
            });
            accountRepo.saveAll(allAccountList);
        } catch (Exception e) {
            log.error("HANDLE IMPORT ACCOUNT FILE:{}, ERROR:{}", e);
        }
    }

    /**
     * 执行导入任务.
     * @param file
     * @return
     */
    private List<AccountModel> handleImportAccountTask(File file) {
        List<AccountModel> templateList = readExcelFile(file);
        FileUtil.deleteFile(file);
        return templateList;
    }

    private List<AccountModel> readExcelFile(File file) {
        List<AccountModel> accountModelList = new ArrayList<>();
        return accountModelList;
    }


    @Override
    public List<AccountModel> fetchAccountList(JSONObject params) {
        JSONArray creatorArray = params.getJSONArray("creator");
        JSONArray uidArray = params.getJSONArray("uid");
        JSONArray personArray = params.getJSONArray("belongPerson");
        JSONArray groupArray = params.getJSONArray("userGroup");
        JSONArray countryArray = params.getJSONArray("country");
        JSONArray statusArray = params.getJSONArray("status");
        List<String> creatorList = JSONArray.parseArray(JSON.toJSONString(creatorArray),String.class);
        List<String> uidList = JSONArray.parseArray(JSON.toJSONString(uidArray),String.class);
        List<String> personList = JSONArray.parseArray(JSON.toJSONString(personArray),String.class);
        List<String> groupList = JSONArray.parseArray(JSON.toJSONString(groupArray),String.class);
        List<String> countryList = JSONArray.parseArray(JSON.toJSONString(countryArray),String.class);
        List<String> statusList = JSONArray.parseArray(JSON.toJSONString(statusArray),String.class);
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(creatorList != null && creatorList.size() >0){
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("creator"));
                    for (String creator : creatorList) {
                        in.value(creator);
                    }
                    predicates.add(in);
                }
                if(uidList != null && uidList.size() >0){
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("uid"));
                    for (String uid : uidList) {
                        in.value(uid);
                    }
                    predicates.add(in);
                }
                if(personList != null && personList.size() >0){
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("belongPerson"));
                    for (String person : personList) {
                        in.value(person);
                    }
                    predicates.add(in);
                }
                if(groupList != null && groupList.size() >0){
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("userGroup"));
                    for (String userGroup : groupList) {
                        in.value(userGroup);
                    }
                    predicates.add(in);
                }
                if(countryList != null && countryList.size() >0){
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("country"));
                    for (String country : countryList) {
                        in.value(country);
                    }
                    predicates.add(in);
                }
                if(statusList != null && statusList.size() >0){
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("status"));
                    for (String status : statusList) {
                        in.value(status);
                    }
                    predicates.add(in);
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<AccountModel> accountModelList = accountRepo.findAll(specification);
        return accountModelList;
    }

    /**
     * 分配账号.
     * @param params
     */
    @Override
    public void updateAssignAccount(JSONObject params) {
        JSONArray uidArray = params.getJSONArray("uid");
        List<String> uidList = JSONArray.parseArray(JSON.toJSONString(uidArray),String.class);
        String belongPerson = params.getString("belongPerson");
        String userGroup = params.getString("userGroup");
        String country = params.getString("userCountry");
        accountRepo.assignAccount(uidList,belongPerson,userGroup,country,DateUtil.formatYyyyMmDdHhMmSs(new Date()));
    }

    /**
     * 显示参数.
     * @return
     */
    @Override
    public JSONObject fetchAccountParam() {
        List<String> userList = adminUserRepo.getAllUserName();
        List<String> roleList = roleRepo.findAllRoleName();
        List<AccountModel> accountVoList = accountRepo.findAll();
        List<String> creatorList = new ArrayList<>();
        List<String> uidList = new ArrayList<>();
        accountVoList.stream().forEach(a->{
            creatorList.add(a.getCreator());
            uidList.add(a.getUid());
        });
        JSONObject params = new JSONObject();
        params.put("belongPerson",userList);
        params.put("userGroup",roleList);
        params.put("creator",creatorList);
        params.put("uid",uidList);
        return params;
    }

    /**
     * 删除账号
     * @param uid
     * @return
     */
    @Override
    public String deleteAccount(String uid) {
        String result = "";
        try {
            accountRepo.deleteByUid(uid);
        }catch (Exception e){
            log.error("DELETE ACCOUNT ERROR");
        }
        return result;
    }

    @Override
    public String saveAccount(JSONObject params) {
        String result = "";
        try {
            AccountModel accountModel = new AccountModel();
            String creator = params.getString("creator");
            String uid = params.getString("uid");
            accountModel.setCreator(creator);
            accountModel.setUid(uid);
            accountRepo.save(accountModel);
            result = "success";
        }catch (Exception e){
            result  = "error";
            log.error("SAVE ACCOUNT ERROR");
        }
        return result;
    }


    /**
     * 更新账号信息.
     * @param params
     */
    @Override
    public void updateAccount(JSONObject params) {
        String uid = params.getString("uid");
        String belongPerson = params.getString("belongPerson");
        String userGroup = params.getString("userGroup");
        Integer status = params.getInteger("status");
        String handler = params.getString("handler");
        AccountModel originModel = accountRepo.findFirstByUid(uid);
        AccountModel updateModel = new AccountModel();
        AccountLogModel logModel = new AccountLogModel();
        if(originModel != null){
            updateModel.setCreateTime(originModel.getCreateTime());
            updateModel.setId(originModel.getId());
            updateModel.setCreator(originModel.getCreator());
            updateModel.setUid(originModel.getUid());
            updateModel.setAssignStatus(originModel.getAssignStatus());
            updateModel.setCountry(originModel.getCountry());
            logModel.setCreator(originModel.getCreator());
            logModel.setUid(originModel.getUid());
            if(StringUtils.isNotBlank(belongPerson)){
                updateModel.setBelongPerson(belongPerson);
                updateModel.setUserGroup(userGroup);
                logModel.setBeforePerson(originModel.getBelongPerson());
                logModel.setLocalPerson(belongPerson);
            }
            if(status != null){
                updateModel.setStatus(status);
                logModel.setBeforeStatus(originModel.getStatus());
                logModel.setLocalStatus(status);
                if(status == 1){
                    updateModel.setCloseTime(DateUtil.formatYyyyMmDdHhMmSs(new Date()));
                }else if(status == 2){
                    updateModel.setTerminateTime(DateUtil.formatYyyyMmDdHhMmSs(new Date()));
                }
            }
            logModel.setHandler(handler);
            logModel.setUpdateTime(new Date());
            accountRepo.saveAndFlush(updateModel);
            accountLogRepo.saveAndFlush(logModel);
        }




    }

    /**
     * 查看日志.
     * @param params
     * @return
     */
    @Override
    public List<AccountLogModel> fetchAccountLog(JSONObject params) {
        String uid = params.getString("uid");
        String creator = params.getString("creator");
        JSONArray timeArray = params.getJSONArray("time");
        List<String> timeList = JSONObject.parseArray(JSON.toJSONString(timeArray),String.class);
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(timeList != null && timeList.size() > 0){
                    Date begin = DateUtil.formatTime(timeList.get(0));
                    Date end = DateUtil.formatTime(timeList.get(1));
                    predicates.add(criteriaBuilder.between(root.get("updateTime"), begin, end));
                }
                if(StringUtils.isNotBlank(uid)){
                    predicates.add(criteriaBuilder.equal(root.get("uid"),uid));
                }
                if(StringUtils.isNotBlank(creator)){
                    predicates.add(criteriaBuilder.equal(root.get("creator"),creator));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<AccountLogModel> logModelList = accountLogRepo.findAll(specification);
        return logModelList;
    }


}
