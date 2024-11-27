package com.dct.service.sample.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 11:47
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dct.common.constant.consist.MainConstant;
import com.dct.common.constant.enums.NumberEnum;
import com.dct.common.websocket.ProductApplyNotification;
import com.dct.model.dct.ProductModel;
import com.dct.model.vo.PageQueryVo;
import com.dct.model.vo.PageVO;
import com.dct.repo.account.AccountRepo;
import com.dct.repo.sample.ProductRepo;
import com.dct.repo.security.AdminUserRepo;
import com.dct.repo.security.AdminUserRoleRepo;
import com.dct.service.sample.IBatchHandleService;
import com.dct.service.sample.IProductService;
import com.dct.utils.DateUtil;
import com.dct.utils.SpringMvcFileUpLoad;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 11:47
 */

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private AdminUserRoleRepo adminUserRoleRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private AccountRepo accountRepo;


    @Autowired
    SpringMvcFileUpLoad fileUpLoad;


    @Autowired
    @Lazy
    private IBatchHandleService batchService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 保存或修改.
     *
     * @param params
     * @return
     */
    @Override
    public String save(JSONObject params) {
        String result = "";
        try {
            ProductModel productModel = new ProductModel();
            String productName = params.getString("productName");
            Integer count = params.getInteger("count");
            Integer status = params.getInteger("status");
            String color = params.getString("color");
            String manager = params.getString("manager");
            String region = params.getString("region");
            String storageLocation = params.getString("storageLocation");
            String productClass = params.getString("productClass");
            String link = params.getString("link");
            String pid = params.getString("pid");
            String id = params.getString("id");
            if (StringUtils.isNotBlank(id)) {
                productModel = productRepo.findById(id).get();
            } else {
                productModel.setOutApply(0);
                productModel.setIsApproval(0);
            }
            productModel.setStatus(status);
            productModel.setProductName(productName);
            productModel.setCount(count);
            productModel.setColor(color);
            productModel.setManager(manager);
            productModel.setRegion(region);
            productModel.setStorageLocation(storageLocation);
            productModel.setProductClass(productClass);
            productModel.setLink(link);
            productModel.setPid(pid);
            productRepo.save(productModel);
            result = MainConstant.SUCCESS;
        } catch (Exception e) {
            result = MainConstant.ERROR;
            log.error("CREATOR PRODUCT INFO ERROR:{}", e.getMessage());
        }
        return result;
    }

    @Override
    public PageVO fetchList(JSONObject params) {
        JSONArray pidArray = params.getJSONArray("pid");
        JSONArray productClassArray = params.getJSONArray("productClass");
        JSONArray productNameArray = params.getJSONArray("productName");
        JSONArray linkArray = params.getJSONArray("link");
        JSONArray regionArray = params.getJSONArray("region");
        JSONArray statusArray = params.getJSONArray("status");
        JSONArray time = params.getJSONArray("time");
        Integer page = params.getInteger("page");
        Integer limit = params.getInteger("limit");
        List<String> pidList = JSONObject.parseArray(JSON.toJSONString(pidArray), String.class);
        List<String> productClassList = JSONObject.parseArray(JSON.toJSONString(productClassArray), String.class);
        List<Integer> statusList = JSONObject.parseArray(JSON.toJSONString(statusArray), Integer.class);
        List<String> productNameList = JSONObject.parseArray(JSON.toJSONString(productNameArray), String.class);
        List<String> linkList = JSONObject.parseArray(JSON.toJSONString(linkArray), String.class);
        List<String> regionList = JSONObject.parseArray(JSON.toJSONString(regionArray), String.class);
        List<String> timeList = JSONObject.parseArray(JSON.toJSONString(time), String.class);
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                Predicate statusPredicate = criteriaBuilder.lessThan(root.get("status"), 3);
                predicates.add(statusPredicate);
                if (productClassList != null && productClassList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("productClass"));
                    for (String creator : productClassList) {
                        in.value(creator);
                    }
                    predicates.add(in);
                }
                if (pidList != null && pidList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("pid"));
                    for (String pid : pidList) {
                        in.value(pid);
                    }
                    predicates.add(in);
                }
                if (linkList != null && linkList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("link"));
                    for (String link : linkList) {
                        in.value(link);
                    }
                    predicates.add(in);
                }
                if (productNameList != null && productNameList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("productName"));
                    for (String productName : productNameList) {
                        in.value(productName);
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
                if (regionList != null && regionList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("region"));
                    for (String region : regionList) {
                        in.value(region);
                    }
                    predicates.add(in);
                }
                if (timeList != null && timeList.size() > 0) {
                    Date begin = DateUtil.formatTime(timeList.get(0));
                    Date end = DateUtil.formatTime(timeList.get(1));
                    predicates.add(criteriaBuilder.between(root.get("createTime"), begin, end));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        PageVO pageVO = new PageVO();
        Long total = productRepo.count(specification);
        page = page >= 1 ? page - 1 : 0;
        List<ProductModel> list = productRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        pageVO.setTotal(total);
        pageVO.setList(list);
        pageVO.setPage(page);
        pageVO.setTotal(total);
        return pageVO;
    }



    public PageVO fetchExportList(JSONObject params) {
        JSONArray pidArray = params.getJSONArray("pid");
        JSONArray productClassArray = params.getJSONArray("productClass");
        JSONArray productNameArray = params.getJSONArray("productName");
        JSONArray linkArray = params.getJSONArray("link");
        JSONArray regionArray = params.getJSONArray("region");
        JSONArray statusArray = params.getJSONArray("status");
        JSONArray time = params.getJSONArray("time");
        List<String> pidList = JSONObject.parseArray(JSON.toJSONString(pidArray), String.class);
        List<String> productClassList = JSONObject.parseArray(JSON.toJSONString(productClassArray), String.class);
        List<Integer> statusList = JSONObject.parseArray(JSON.toJSONString(statusArray), Integer.class);
        List<String> productNameList = JSONObject.parseArray(JSON.toJSONString(productNameArray), String.class);
        List<String> linkList = JSONObject.parseArray(JSON.toJSONString(linkArray), String.class);
        List<String> regionList = JSONObject.parseArray(JSON.toJSONString(regionArray), String.class);
        List<String> timeList = JSONObject.parseArray(JSON.toJSONString(time), String.class);
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                Predicate statusPredicate = criteriaBuilder.lessThan(root.get("status"), 3);
                predicates.add(statusPredicate);
                if (productClassList != null && productClassList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("productClass"));
                    for (String creator : productClassList) {
                        in.value(creator);
                    }
                    predicates.add(in);
                }
                if (pidList != null && pidList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("pid"));
                    for (String pid : pidList) {
                        in.value(pid);
                    }
                    predicates.add(in);
                }
                if (linkList != null && linkList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("link"));
                    for (String link : linkList) {
                        in.value(link);
                    }
                    predicates.add(in);
                }
                if (productNameList != null && productNameList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("productName"));
                    for (String productName : productNameList) {
                        in.value(productName);
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
                if (regionList != null && regionList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("region"));
                    for (String region : regionList) {
                        in.value(region);
                    }
                    predicates.add(in);
                }
                if (timeList != null && timeList.size() > 0) {
                    Date begin = DateUtil.formatTime(timeList.get(0));
                    Date end = DateUtil.formatTime(timeList.get(1));
                    predicates.add(criteriaBuilder.between(root.get("createTime"), begin, end));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        PageVO pageVO = new PageVO();
        Long total = productRepo.count(specification);
        List<ProductModel> list = productRepo.findAll(specification);
        pageVO.setTotal(total);
        pageVO.setList(list);
        pageVO.setTotal(total);
        return pageVO;
    }

    @Override
    public PageVO fetchOutboundList(JSONObject params) {
        JSONArray pidArray = params.getJSONArray("pid");
        JSONArray statusArray = params.getJSONArray("status");
        JSONArray applyUserArray = params.getJSONArray("user");
        JSONArray productNameArray = params.getJSONArray("productName");
        JSONArray time = params.getJSONArray("time");
        Integer page = params.getInteger("page");
        Integer limit = params.getInteger("limit");
        List<String> pidList = JSONObject.parseArray(JSON.toJSONString(pidArray), String.class);
        List<Integer> statusList = JSONObject.parseArray(JSON.toJSONString(statusArray), Integer.class);
        List<String> applyUserList = JSONObject.parseArray(JSON.toJSONString(applyUserArray), String.class);
        List<String> productNameList = JSONObject.parseArray(JSON.toJSONString(productNameArray), String.class);
        List<String> timeList = JSONObject.parseArray(JSON.toJSONString(time), String.class);
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                Predicate statusPredicate = criteriaBuilder.greaterThan(root.get("status"), 2);
                predicates.add(statusPredicate);
                if (productNameList != null && productNameList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("productName"));
                    for (String productName : productNameList) {
                        in.value(productName);
                    }
                    predicates.add(in);
                }
                if (pidList != null && pidList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("pid"));
                    for (String pid : pidList) {
                        in.value(pid);
                    }
                    predicates.add(in);
                }
                if (applyUserList != null && applyUserList.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("applyUser"));
                    for (String applyUser : applyUserList) {
                        in.value(applyUser);
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
                if (timeList != null && timeList.size() > 0) {
                    Date begin = DateUtil.formatTime(timeList.get(0));
                    Date end = DateUtil.formatTime(timeList.get(1));
                    predicates.add(criteriaBuilder.between(root.get("createTime"), begin, end));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        PageVO pageVO = new PageVO();
        Long total = productRepo.count(specification);
        page = page >= 1 ? page - 1 : 0;
        List<ProductModel> list = productRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        pageVO.setTotal(total);
        pageVO.setList(list);
        pageVO.setPage(page);
        pageVO.setTotal(total);
        return pageVO;
    }

    /**
     * 更新样品信息.
     *
     * @param params
     * @return
     */
    @Override
    public String updateProduct(JSONObject params) {
        String result = "";
        JSONArray array = params.getJSONArray("batchApply");
        if (array != null) {
            try {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject batchItem = array.getJSONObject(i);
                    String id = batchItem.getString("id");
                    Integer status = batchItem.getInteger("status");
                    String user = batchItem.getString("user");
                    String applyUser = batchItem.getString("applyUser");
                    ProductModel originModel = productRepo.findById(id).get();
                    if (originModel != null) {
                        if (StringUtils.isNotBlank(user)) {
                            originModel.setUser(user);
                        }
                        if (StringUtils.isNotBlank(applyUser)) {
                            originModel.setApplyUser(applyUser);
                        }
                    }
                    // 修改为在库状态
                    if (status != null) {
                        originModel.setStatus(status);
                        if (status == 1 || status == 4) {
                            System.out.println("status   " + status);
                            String pid = originModel.getPid();
                            String color = originModel.getColor();
                            List<ProductModel> notApplyModelList = productRepo.findNoApplyModel(pid, color);
                            //若存在在库状态的产品则需要合并下
                            if (notApplyModelList != null && notApplyModelList.size() > 0) {
                                ProductModel notApplyModel = notApplyModelList.get(0);
                                originModel.setCount(notApplyModel.getCount() + originModel.getCount());
                                productRepo.deleteById(notApplyModel.getId());
                            }
                            originModel.setCount(originModel.getCount());
                            originModel.setApplyCount(0);
                            originModel.setOutApply(0);
                            originModel.setIsApproval(0);
                            originModel.setStatus(1);
                        }
                        productRepo.saveAndFlush(originModel);
                    } else {
                        productRepo.saveAndFlush(originModel);
                    }
                }
                result = MainConstant.SUCCESS;
            } catch (Exception e) {
                result = MainConstant.ERROR;
                log.error("UPDATE PRODUCT ERROR");
            }
        } else {
            try {
                String id = params.getString("id");
                Integer status = params.getInteger("status");
                String user = params.getString("user");
                String applyUser = params.getString("applyUser");
                ProductModel originModel = productRepo.findById(id).get();
                if (originModel != null) {
                    if (StringUtils.isNotBlank(user)) {
                        originModel.setUser(user);
                    }
                    if (StringUtils.isNotBlank(applyUser)) {
                        originModel.setApplyUser(applyUser);
                    }
                }
                // 修改为在库状态
                if (status != null) {
                    originModel.setStatus(status);
                    if (status == 1) {
                        String pid = originModel.getPid();
                        String color = originModel.getColor();
                        List<ProductModel> notApplyModelList = productRepo.findNoApplyModel(pid, color);
                        //若存在在库状态的产品则需要合并下
                        if (notApplyModelList != null && notApplyModelList.size() > 0) {
                            ProductModel notApplyModel = notApplyModelList.get(0);
                            originModel.setCount(notApplyModel.getCount() + originModel.getCount());
                            productRepo.deleteById(notApplyModel.getId());
                        }
                        originModel.setCount(originModel.getCount());
                        originModel.setApplyCount(0);
                        originModel.setOutApply(0);
                        originModel.setIsApproval(0);
                    }
                    productRepo.saveAndFlush(originModel);
                } else {
                    productRepo.saveAndFlush(originModel);
                }
                result = MainConstant.SUCCESS;
            } catch (Exception e) {
                result = MainConstant.ERROR;
                log.error("UPDATE PRODUCT ERROR");
            }
        }
        return result;
    }

    /**
     * 获取参数.
     *
     * @return
     */
    @Override
    public JSONObject fetchParams() {
        List<String> userList = adminUserRepo.findAll().stream().map(a -> a.getUsername()).collect(Collectors.toList());
        String sampleId = "1793e48f-bc4f-4b76-a411-3c56b81990a6";
        String userId = adminUserRoleRepo.getUserId(sampleId);
        List<String> managerList = adminUserRepo.getRoleUsername(userId);
        List<String> productNameList = productRepo.findAll().stream().map(a -> a.getProductName()).distinct().collect(Collectors.toList());
        List<String> pidList = productRepo.findAll().stream().map(a -> a.getPid()).distinct().collect(Collectors.toList());
        List<String> linkList = productRepo.findAll().stream().map(a -> a.getLink()).distinct().collect(Collectors.toList());
        List<String> productClassList = productRepo.findAll().stream().map(a -> a.getProductClass()).distinct().collect(Collectors.toList());
        JSONObject params = new JSONObject();
        params.put("productName", productNameList);
        params.put("user", userList);
        params.put("link", linkList);
        params.put("pid", pidList);
        params.put("productClass", productClassList);
        params.put("manager", managerList);
        return params;
    }



    @Override
    public void uploadProductPhoto(String pid, MultipartFile file) {
        String fileType = file.getContentType();
        if (!org.springframework.util.StringUtils.isEmpty(pid)) {
            String fileName = pid + "." + fileType.split("/")[1];
            String filePath = "product/";
            String s3Url = fileUpLoad.fileUpload(file, fileName, filePath);
            productRepo.updatePictureUrl(pid, s3Url);
        }
    }

    /**
     * 申请.
     *
     * @param params
     * @return
     */
    @Override
    public String applyProduct(JSONObject params) {
        String result = "";
        String id = params.getString("id");
        Integer applyCount = params.getInteger("applyCount");
        String applyUser = params.getString("applyUser");
        String user = params.getString("user");
        String productName = params.getString("productName");
        Integer count = params.getInteger("count");
        String applyKey = MainConstant.APPLY_PRODUCT + MainConstant.COLON + id;
        RLock applyLock = redissonClient.getLock(applyKey);
        ProductModel originModel = productRepo.findById(id).get();
        if (applyCount == null) {
            applyCount = count;
        }
        try {
            //加锁，同一个ID操作时候其他人不能操作
            if (applyLock.tryLock(NumberEnum.FIVE.getNum(), NumberEnum.TEN.getNum(), TimeUnit.SECONDS)) {
                if (count >= applyCount) {
                    productRepo.updateApprove(id, count - applyCount);
                    ProductModel approveModel = new ProductModel();
                    approveModel.setManager(originModel.getManager());
                    approveModel.setUser(originModel.getUser());
                    approveModel.setIsApproval(1);
                    approveModel.setOutApply(1);
                    approveModel.setStatus(2);
                    approveModel.setApplyUser(originModel.getApplyUser());
                    approveModel.setPid(originModel.getPid());
                    approveModel.setColor(originModel.getColor());
                    approveModel.setCount(applyCount);
                    approveModel.setApplyCount(applyCount);
                    approveModel.setPicture(originModel.getPicture());
                    approveModel.setProductName(originModel.getProductName());
                    approveModel.setLink(originModel.getLink());
                    approveModel.setStorageLocation(originModel.getStorageLocation());
                    approveModel.setRegion(originModel.getRegion());
                    approveModel.setProductClass(originModel.getProductClass());
                    approveModel.setPid(originModel.getPid());
                    productRepo.save(approveModel);
                } else if (count < applyCount) {
                    productRepo.applyProduct(id, applyCount, applyUser);
                }
            } else {
                log.info("APPLY PRODUCT LOCK:{}", applyLock);
            }
            JSONObject notification = new JSONObject();
            notification.put("product", productName);
            notification.put("applyUser", applyUser);
            notification.put("user", user);
            notification.put("count", applyCount);
            ProductApplyNotification.sendInfo(JSON.toJSONString(notification));
            result = MainConstant.SUCCESS;
        } catch (Exception e) {
            result = MainConstant.ERROR;
        } finally {
            applyLock.unlock();
        }
        return result;
    }

    /**
     * 审批.
     *
     * @param params
     * @return
     */
    @Override
    public String approveProduct(JSONObject params) {
        String result = "";
        JSONArray idArray = params.getJSONArray("id");
        List<String> idList = JSONArray.parseArray(JSON.toJSONString(idArray), String.class);
        try {
            idList.stream().forEach(a -> {
                batchService.approveApply(idList);
            });
            result = MainConstant.SUCCESS;
        } catch (Exception e) {
            result = MainConstant.ERROR;
            log.error("BATCH APPROVE PRODUCT ERROR:{}", e.getMessage());
        }
        return result;
    }

    @Override
    public String batchApplyProduct(JSONObject params) {
        String result = "";
        JSONArray array = params.getJSONArray("batchApply");
        try {
            array.stream().forEach(a->{
                applyProduct((JSONObject) a);
            });
            result = MainConstant.SUCCESS;
        }catch (Exception e){
            result = MainConstant.ERROR;
            log.error("BATCH APPLY PRODUCT ERROR:{}",e.getMessage());
        }
        return result;
    }

    @Override
    public ProductModel fetchProduct(String id) {
        ProductModel productModel = productRepo.findById(id).get();
        return productModel;
    }

    @Override
    public String deleteProduct(String id) {
        String result = "";
        try {
            productRepo.deleteById(id);
            result = MainConstant.SUCCESS;
        } catch (Exception e) {
            result = MainConstant.ERROR;
            log.error("DELETE PRODUCT ERROR:{}", e.getMessage());
        }
        return result;
    }

}
