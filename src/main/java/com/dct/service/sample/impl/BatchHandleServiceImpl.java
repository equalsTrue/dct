package com.dct.service.sample.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/13 11:29
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dct.common.constant.consist.MainConstant;
import com.dct.common.constant.enums.NumberEnum;
import com.dct.common.websocket.ProductApplyNotification;
import com.dct.model.dct.ProductModel;
import com.dct.repo.sample.ProductRepo;
import com.dct.service.sample.IBatchHandleService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/13 11:29 
 */
@Service
@Slf4j
public class BatchHandleServiceImpl implements IBatchHandleService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 批准申请.
     * @param idList
     */
    @Override
    @Async
    public void approveApply(List<String> idList) {
        idList.stream().forEach(a->{
            String id = a;
            ProductModel productModel = productRepo.findById(id).get();
            Integer count = productModel.getCount();
            Integer applyCount = productModel.getApplyCount();
            productRepo.updateAllApprove(id,count,applyCount);

        });
    }

    @Override
    public void applyProduct(Object obj) {
        JSONObject params = JSONObject.parseObject(JSON.toJSONString(obj));
        String id = params.getString("id");
        Integer applyCount = params.getInteger("applyCount");
        String applyUser = params.getString("applyUser");
        String user = params.getString("user");
        String productName = params.getString("productName");
        String applyKey = MainConstant.APPLY_PRODUCT + MainConstant.COLON + id;
        RLock applyLock = redissonClient.getLock(applyKey);
        try {
            //加锁，同一个ID操作时候其他人不能操作
            if (applyLock.tryLock(NumberEnum.FIVE.getNum(), NumberEnum.TEN.getNum(), TimeUnit.SECONDS)) {
                productRepo.applyProduct(id,applyCount,applyUser);
            } else {
                log.info("APPLY PRODUCT LOCK:{}", applyLock);
            }
            JSONObject notification = new JSONObject();
            notification.put("product",productName);
            notification.put("applyUser",applyUser);
            notification.put("user",user);
            notification.put("count",applyCount);
            ProductApplyNotification.sendInfo(JSON.toJSONString(notification));
        } catch (Exception e) {
        } finally {
            applyLock.unlock();
        }
    }
}
