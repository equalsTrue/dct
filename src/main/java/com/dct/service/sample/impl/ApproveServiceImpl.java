package com.dct.service.sample.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/13 11:29
 */

import com.dct.model.dct.ProductModel;
import com.dct.repo.sample.ProductRepo;
import com.dct.service.sample.IApproveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/13 11:29 
 */
@Service
public class ApproveServiceImpl implements IApproveService {

    @Autowired
    private ProductRepo productRepo;

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
            if(count > applyCount){
                productRepo.updateApprove(id,count-applyCount);
                ProductModel approveModel = new ProductModel();
                approveModel.setManager(productModel.getManager());
                approveModel.setUser(productModel.getUser());
                approveModel.setIsApproval(1);
                approveModel.setOutApply(1);
                approveModel.setStatus(2);
                approveModel.setApplyUser(productModel.getApplyUser());
                approveModel.setPid(productModel.getPid());
                approveModel.setColor(productModel.getColor());
                approveModel.setCount(applyCount);
                approveModel.setApplyCount(applyCount);
                approveModel.setPicture(productModel.getPicture());
                approveModel.setProductName(productModel.getProductName());
                approveModel.setPid(productModel.getPid());
                productRepo.save(approveModel);
            }else if(count == applyCount){
                productRepo.updateAllApprove(id,count);
            }
        });
    }
}
