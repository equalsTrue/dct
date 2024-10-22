package com.dct.service.sample;

import com.alibaba.fastjson.JSONObject;
import com.dct.model.vo.GmvDetailVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/13 11:28
 */

public interface IBatchHandleService {
    void approveApply(List<String> idList);

    void applyProduct(Object params);

    void handlePerDayGmvData(String day, List<String> creatorList);

    void importIndexGmvData(String a);

    CompletableFuture<List<GmvDetailVo>> generateGvmIndex(List<String> groupList, JSONObject whereParam);

    CompletableFuture<List<GmvDetailVo>> generateGvmData(List<String> groupList, StringBuffer sql);

    CompletableFuture<String> queryPidAndName(String productId);
}
