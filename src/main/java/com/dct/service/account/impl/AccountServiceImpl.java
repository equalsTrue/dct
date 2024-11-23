package com.dct.service.account.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 16:23
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dct.common.config.datasource.ClickHouseConfig;
import com.dct.model.dct.AccountLogModel;
import com.dct.model.dct.AccountModel;
import com.dct.model.vo.PageVO;
import com.dct.repo.account.AccountLogRepo;
import com.dct.repo.account.AccountRepo;
import com.dct.repo.security.AdminRoleRepo;
import com.dct.repo.security.AdminUserRepo;
import com.dct.service.account.IAccountService;
import com.dct.service.security.IAdminUserService;
import com.dct.utils.DateUtil;
import com.dct.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
    private AdminRoleRepo adminRoleRepo;

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
    public void importAccountFile(List<MultipartFile> files, String manager) {
        try {
            List<AccountModel> allAccountList = new ArrayList<>();
            files.stream().forEach(a -> {
                File tempFile = FileUtil.multipartFileToFile(a);
                List<AccountModel> accountModelList = handleImportAccountTask(tempFile);
                accountModelList.stream().forEach(b -> {
                    b.setManager(manager);
                });
                allAccountList.addAll(accountModelList);
            });
            accountRepo.saveAll(allAccountList);
        } catch (Exception e) {
            log.error("HANDLE IMPORT ACCOUNT FILE:{}, ERROR:{}", e);
        }
    }

    /**
     * 执行导入任务.
     *
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
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            int index = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                AccountModel accountModel = new AccountModel();
                String[] item = line.split(",");
                String creator = item[0];
                String uid = item[1];
                accountModel.setCreator(creator);
                accountModel.setUid(uid);
                accountModelList.add(accountModel);
                index++;
            }
        } catch (Exception e) {
            log.error("SUBMIT FILE ERROR:{}", e.getMessage());
        }
        return accountModelList;
    }


    @Override
    public PageVO fetchAccountList(JSONObject params) {
        JSONArray creatorArray = params.getJSONArray("creator");
        JSONArray uidArray = params.getJSONArray("uid");
        JSONArray personArray = params.getJSONArray("belongPerson");
        JSONArray groupArray = params.getJSONArray("userGroup");
        JSONArray countryArray = params.getJSONArray("country");
        JSONArray statusArray = params.getJSONArray("status");
        JSONArray assignStatusArray = params.getJSONArray("assignStatus");
        List<String> creatorList = JSONArray.parseArray(JSON.toJSONString(creatorArray), String.class);
        List<String> uidList = JSONArray.parseArray(JSON.toJSONString(uidArray), String.class);
        List<String> personList = JSONArray.parseArray(JSON.toJSONString(personArray), String.class);
        List<String> groupList = JSONArray.parseArray(JSON.toJSONString(groupArray), String.class);
        List<String> countryList = JSONArray.parseArray(JSON.toJSONString(countryArray), String.class);
        List<Integer> statusList = JSONArray.parseArray(JSON.toJSONString(statusArray), Integer.class);
        List<Integer> assignStatusList = JSONArray.parseArray(JSON.toJSONString(assignStatusArray), Integer.class);

        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (creatorList != null && creatorList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("creator"));
                    for (String creator : creatorList) {
                        in.value(creator);
                    }
                    predicates.add(in);
                }
                if (uidList != null && uidList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("uid"));
                    for (String uid : uidList) {
                        in.value(uid);
                    }
                    predicates.add(in);
                }
                if (personList != null && personList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("belongPerson"));
                    for (String person : personList) {
                        in.value(person);
                    }
                    predicates.add(in);
                }
                if (groupList != null && groupList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("userGroup"));
                    for (String userGroup : groupList) {
                        in.value(userGroup);
                    }
                    predicates.add(in);
                }
                if (countryList != null && countryList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("country"));
                    for (String country : countryList) {
                        in.value(country);
                    }
                    predicates.add(in);
                }
                if (assignStatusList != null && assignStatusList.size() > 0) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("assignStatus"));
                    for (Integer assignStatus : assignStatusList) {
                        in.value(assignStatus);
                    }
                    predicates.add(in);
                }
                if (statusList != null && statusList.size() > 0) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (Integer status : statusList) {
                        in.value(status);
                    }
                    predicates.add(in);
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };

        Integer page = params.getInteger("page");
        Integer limit = params.getInteger("limit");
        Long total = 0L;
        PageVO pageVO = new PageVO();
        List<AccountModel> list = new ArrayList<>();
        if (page == null && limit == null) {
            list = accountRepo.findAll(specification);
        } else {
            total = accountRepo.count(specification);
            page = page >= 1 ? page - 1 : 0;
            list = accountRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
            pageVO.setTotal(total);
            list.stream().forEach(a -> {
                List<String> userGroupList = adminRoleRepo.queryRoleNames(a.getBelongPerson());
                a.setUserGroup(userGroupList != null && userGroupList.size() > 0 ? userGroupList.get(0) : "");
            });
        }
        pageVO.setList(list);
        pageVO.setPage(page);
        pageVO.setTotal(total);
        return pageVO;
    }

    @Override
    public PageVO fetchExportAccountList(JSONObject params) {
        JSONArray creatorArray = params.getJSONArray("creator");
        JSONArray uidArray = params.getJSONArray("uid");
        JSONArray personArray = params.getJSONArray("belongPerson");
        JSONArray groupArray = params.getJSONArray("userGroup");
        JSONArray countryArray = params.getJSONArray("country");
        JSONArray statusArray = params.getJSONArray("status");
        JSONArray assignStatusArray = params.getJSONArray("assignStatus");
        List<String> creatorList = JSONArray.parseArray(JSON.toJSONString(creatorArray), String.class);
        List<String> uidList = JSONArray.parseArray(JSON.toJSONString(uidArray), String.class);
        List<String> personList = JSONArray.parseArray(JSON.toJSONString(personArray), String.class);
        List<String> groupList = JSONArray.parseArray(JSON.toJSONString(groupArray), String.class);
        List<String> countryList = JSONArray.parseArray(JSON.toJSONString(countryArray), String.class);
        List<Integer> statusList = JSONArray.parseArray(JSON.toJSONString(statusArray), Integer.class);
        List<Integer> assignStatusList = JSONArray.parseArray(JSON.toJSONString(assignStatusArray), Integer.class);

        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (creatorList != null && creatorList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("creator"));
                    for (String creator : creatorList) {
                        in.value(creator);
                    }
                    predicates.add(in);
                }
                if (uidList != null && uidList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("uid"));
                    for (String uid : uidList) {
                        in.value(uid);
                    }
                    predicates.add(in);
                }
                if (personList != null && personList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("belongPerson"));
                    for (String person : personList) {
                        in.value(person);
                    }
                    predicates.add(in);
                }
                if (groupList != null && groupList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("userGroup"));
                    for (String userGroup : groupList) {
                        in.value(userGroup);
                    }
                    predicates.add(in);
                }
                if (countryList != null && countryList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("country"));
                    for (String country : countryList) {
                        in.value(country);
                    }
                    predicates.add(in);
                }
                if (assignStatusList != null && assignStatusList.size() > 0) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("assignStatus"));
                    for (Integer assignStatus : assignStatusList) {
                        in.value(assignStatus);
                    }
                    predicates.add(in);
                }
                if (statusList != null && statusList.size() > 0) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (Integer status : statusList) {
                        in.value(status);
                    }
                    predicates.add(in);
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };

        Long total = 0L;
        PageVO pageVO = new PageVO();
        List<AccountModel> list = new ArrayList<>();
        list = accountRepo.findAll(specification);
        pageVO.setList(list);
        pageVO.setTotal(total);
        return pageVO;
    }

    /**
     * 分配账号.
     *
     * @param params
     */
    @Override
    public void updateAssignAccount(JSONObject params) {
        String uid = params.getString("uid");
        String belongPerson = params.getString("belongPerson");
        List<String> userGroupList = adminRoleRepo.queryRoleNames(belongPerson);
        String userGroup = "";
        if (userGroupList != null && userGroupList.size() > 0) {
            userGroup = userGroupList.get(0);
        }
        String country = params.getString("country");
        List<String> uidList = new ArrayList<>();
        uidList.add(uid);
        accountRepo.assignAccount(uidList, belongPerson, userGroup, country, DateUtil.formatYyyyMmDdHhMmSs(new Date()));
    }

    /**
     * 显示参数.
     *
     * @return
     */
    @Override
    public JSONObject fetchAccountParam() {
        List<String> userList = adminUserRepo.getAllUserName();
        List<String> roleList = roleRepo.findAllRoleName();
        List<AccountModel> accountVoList = accountRepo.findAll();
        List<String> creatorList = new ArrayList<>();
        List<String> uidList = new ArrayList<>();
        accountVoList.stream().forEach(a -> {
            creatorList.add(a.getCreator());
            uidList.add(a.getUid());
        });
        JSONObject params = new JSONObject();
        params.put("belongPerson", userList);
        params.put("userGroup", roleList);
        params.put("creator", creatorList);
        params.put("uid", uidList);
        return params;
    }

    /**
     * 删除账号
     *
     * @param id
     * @return
     */
    @Override
    public String deleteAccount(String id) {
        String result = "";
        try {
            accountRepo.deleteById(id);
        } catch (Exception e) {
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
            String country = params.getString("country");
            String belongUser = params.getString("belongPerson");
            String manager = params.getString("manager");
            String account_type = params.getString("account_type");
            String category = params.getString("category");
            String notes = params.getString("notes");
            String id = params.getString("id");
            if (StringUtils.isNotBlank(id)) {
                AccountModel originModel = accountRepo.findById(id).get();
                if (originModel != null) {
                    String oldCreator = originModel.getCreator();
                    if (!Objects.equals(oldCreator, creator)) {
                        AccountLogModel logModel = new AccountLogModel();
                        StringBuffer sql = new StringBuffer();
                        sql.append(" ALTER TABLE gmv_detail UPDATE creator = '"+ creator + "' WHERE creator='" + oldCreator + "'");
                        executeSql(sql);
                        StringBuffer sql1 = new StringBuffer();
                        sql1.append(" ALTER TABLE video_detail UPDATE creator = '"+ creator + "' WHERE creator='" + oldCreator + "'");
                        executeSql(sql1);

                        logModel.setHandler(oldCreator);
                        logModel.setCreator(creator);
                        logModel.setUid(originModel.getUid());
                        logModel.setManager(originModel.getManager());
                        logModel.setUpdateTime(new Date());
                        accountLogRepo.saveAndFlush(logModel);
                    }

                    accountModel = originModel;
                    accountModel.setId(id);

                }
            } else {
                Integer assignStatus = 0;
                accountModel.setAssignStatus(assignStatus);
            }
            accountModel.setCreator(creator);
            accountModel.setUid(uid);
            accountModel.setManager(manager);
            accountModel.setCountry(country);
            accountModel.setBelongPerson(belongUser);
            accountModel.setAccount_type(account_type);
            accountModel.setCategory(category);
            accountModel.setNotes(notes);
            accountRepo.save(accountModel);
            result = "success";
        } catch (Exception e) {
            result = "error";
            log.error("SAVE ACCOUNT ERROR");
        }
        return result;
    }

    @Override
    public AccountModel fetchAccountModel(String id) {
        AccountModel accountModel = accountRepo.findById(id).get();
        return accountModel;
    }


    /**
     * 更新账号信息.
     *
     * @param params
     */
    @Override
    public void updateAccount(JSONObject params) {
        String id = params.getString("id");
        String belongPerson = params.getString("belongPerson");
        String accountType = params.getString("accountType");
        Integer status = params.getInteger("status");
        String country = params.getString("country");
        String category = params.getString("category");
        String notes = params.getString("notes");
        AccountModel originModel = accountRepo.findById(id).get();
        AccountModel updateModel = new AccountModel();
        AccountLogModel logModel = new AccountLogModel();
        if (originModel != null) {
            updateModel.setCreateTime(originModel.getCreateTime());
            updateModel.setId(originModel.getId());
            updateModel.setCreator(originModel.getCreator());
            updateModel.setUid(originModel.getUid());
            updateModel.setAssignStatus(originModel.getAssignStatus());
            updateModel.setCountry(originModel.getCountry());
            updateModel.setDeliverTime(originModel.getDeliverTime());
            updateModel.setStatus(originModel.getStatus());
            updateModel.setAccount_type(originModel.getAccount_type());
            updateModel.setManager(originModel.getManager());
			updateModel.setCategory(StringUtils.isNotBlank(category) ? category : originModel.getCategory());
            updateModel.setNotes(StringUtils.isNotBlank(notes) ? notes : originModel.getNotes());
            logModel.setCreator(originModel.getCreator());
            logModel.setUid(originModel.getUid());
            // 是否重新分配人
            if (StringUtils.isNotBlank(belongPerson)) {
                updateModel.setBelongPerson(belongPerson);
                updateModel.setUserGroup(adminRoleRepo.queryRoleNames(belongPerson).get(0));
                logModel.setBeforePerson(originModel.getBelongPerson());
                logModel.setLocalPerson(belongPerson);
            } else {
                updateModel.setBelongPerson(originModel.getBelongPerson());
                updateModel.setUserGroup(originModel.getUserGroup());
                logModel.setLocalPerson(originModel.getBelongPerson());
                logModel.setBeforePerson(originModel.getBelongPerson());
            }
            if (status != null) {
                updateModel.setStatus(status);
                logModel.setBeforeStatus(originModel.getStatus());
                logModel.setLocalStatus(status);
                if (status == 1) {
                    updateModel.setCloseTime(DateUtil.formatYyyyMmDdHhMmSs(new Date()));
                } else if (status == 2) {
                    updateModel.setTerminateTime(DateUtil.formatYyyyMmDdHhMmSs(new Date()));
                }
            } else {
                logModel.setBeforeStatus(originModel.getStatus());
                logModel.setLocalStatus(originModel.getStatus());
            }
            if (StringUtils.isNotBlank(country)) {
                updateModel.setCountry(country);
            }
            if (StringUtils.isNotBlank(accountType)) {
                updateModel.setAccount_type(accountType);
            }
            logModel.setManager(originModel.getManager());
            logModel.setUpdateTime(new Date());
            accountRepo.saveAndFlush(updateModel);
            accountLogRepo.saveAndFlush(logModel);
        }

    }

    /**
     * 查看日志.
     *
     * @param params
     * @return
     */
    @Override
    public List<AccountLogModel> fetchAccountLog(JSONObject params) {
        String uid = params.getString("uid");
        String creator = params.getString("creator");
        JSONArray timeArray = params.getJSONArray("time");
        List<String> timeList = JSONObject.parseArray(JSON.toJSONString(timeArray), String.class);
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (timeList != null && timeList.size() > 0) {
                    Date begin = DateUtil.formatTime(timeList.get(0));
                    Date end = DateUtil.formatTime(timeList.get(1));
                    predicates.add(criteriaBuilder.between(root.get("updateTime"), begin, end));
                }
                if (StringUtils.isNotBlank(uid)) {
                    predicates.add(criteriaBuilder.equal(root.get("uid"), uid));
                }
                if (StringUtils.isNotBlank(creator)) {
                    predicates.add(criteriaBuilder.equal(root.get("creator"), creator));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<AccountLogModel> logModelList = accountLogRepo.findAll(specification);
        return logModelList;
    }

    public void executeSql(StringBuffer sql) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ClickHouseConfig.getConnection();
//            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql.toString());
            int count = stmt.executeUpdate();
            conn.commit();
            log.info("EXECUTE UPDATE SQL:{},COUNT:{}", sql, count);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //释放资源
            ClickHouseConfig.release(conn, stmt, rs);
        }
    }

}
