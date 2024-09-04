package com.dct.model.vo;/**
 * @program ladder-combiner
 * @description:
 * @author: lichen
 * @create: 2024/04/22 16:40
 */

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @program ladder-combiner 
 * @description:
 * @author: lichen
 * @create: 2024/04/22 16:40 
 */

@Data
public class PageQueryVo {

    private JSONObject pageFilterVo;

    private List<String> pageGroupVo;

    private List<String> pageMetricsVo;

    private PageVO pageVO;

    /**
     * userName
     */
    private String name;

}
