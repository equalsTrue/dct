package com.dct.service.sample;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/13 11:28
 */

public interface IBatchHandleService {
    void approveApply(List<String> idList);

    void applyProduct(Object params);
}
