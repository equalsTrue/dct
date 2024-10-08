package com.dct.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Charles
 * @date 2019/2/21
 * @description :
 */
@Data
public class PageVO<T> {

    List<T> list;

    Long total;

    Integer limit;

    Integer page;

    String sortColumn;

    String sortType;

    Boolean checkEmail;

    BigInteger distanceUserNum;

    JSONObject totalList;


}
