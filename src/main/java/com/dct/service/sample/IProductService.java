package com.dct.service.sample;

import com.alibaba.fastjson.JSONObject;
import com.dct.model.dct.ProductModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 11:47
 */
public interface IProductService {

    String save(JSONObject params);

    List<ProductModel> fetchList(JSONObject params);

    String updateProduct(JSONObject params);

    JSONObject fetchParams();

    void uploadProductPhoto(String uid, MultipartFile file);

    String applyProduct(JSONObject params);

    String approveProduct(JSONObject params);
}
