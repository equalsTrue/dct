package com.dct.controller.sample;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 11:33
 */

import com.alibaba.fastjson.JSONObject;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.sample.IProductService;
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

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/12 11:33 
 */

@RestController
@RequestMapping(value = "/dct/product")
public class ProductManagerController {

    @Autowired
    private IProductService productService;

    /**
     * 保存.
     * @param params
     * @return
     */
    @PostMapping("/save")
    public ResponseInfoVO saveProduct(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.save(params));
    }

    /**
     * 获取参数.
     */
    @GetMapping("/params")
    public ResponseInfoVO fetchParams(){
        return ResponseInfoUtil.success(productService.fetchParams());
    }

    @GetMapping("/sampleParams")
    public ResponseInfoVO fetchSampleParams(){
        return ResponseInfoUtil.success(productService.fetchSampleParams());
    }



    /**
     * 查询.
     * @param params
     * @return
     */
    @PostMapping("/list")
    public ResponseInfoVO fetchList(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.fetchList(params));
    }

    @PostMapping("/outboundList")
    public ResponseInfoVO fetchOutboundList(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.fetchOutboundList(params));
    }

    /**
     * 查询具体信息.
     * @param id
     * @return
     */
    @GetMapping("/get")
    public ResponseInfoVO fetchList(@RequestParam String id){
        return ResponseInfoUtil.success(productService.fetchProduct(id));
    }



    /**
     * 上传样品照片.
     * @param pid
     * @param file
     * @return
     */
    @PostMapping(value = "/submit/{pid}/productPicture")
    public ResponseInfoVO submitPicture(@PathVariable(required = true) String pid, MultipartFile file){
        productService.uploadProductPhoto(pid,file);
        return ResponseInfoUtil.success();
    }


    /**
     * 申请.
     * @param params
     * @return
     */
    @PostMapping(value = "/apply")
    public ResponseInfoVO applyProduct(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.applyProduct(params));
    }

    /**
     * 批量申请.
     * @param params
     * @return
     */
    @PostMapping(value = "/batch/apply")
    public ResponseInfoVO batchApplyProduct(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.batchApplyProduct(params));
    }

    /**
     * 批准.
     * @param params
     * @return
     */
    @PostMapping(value = "/approve")
    public ResponseInfoVO approveProduct(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.approveProduct(params));
    }

    @GetMapping(value = "/delete")
    public ResponseInfoVO deleteProduct(@RequestParam String id){
        return ResponseInfoUtil.success(productService.deleteProduct(id));
    }

    @PostMapping(value = "/update/info")
    public ResponseInfoVO updateProduct(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(productService.updateProduct(params));
    }
}
