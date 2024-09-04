package com.dct.controller.analysis;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 12:34
 */

import com.alibaba.fastjson.JSONObject;
import com.dct.model.vo.PageQueryVo;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.analysis.IGmvAnalysisService;
import com.dct.utils.ResponseInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * gmv分析
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 12:34 
 */
@RestController("/dct/analysis")
public class GmvAnalysisController {

    @Autowired
    private IGmvAnalysisService gmvAnalysisService;

    /**
     * 根据条件查询
     * @return
     */
    @PostMapping("/gmv")
    public ResponseInfoVO analysis(@RequestBody PageQueryVo pageQueryVo){
        return ResponseInfoUtil.success(gmvAnalysisService.queryGmvData(pageQueryVo));
    }
}
