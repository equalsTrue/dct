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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * gmv分析
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 12:34 
 */
@RestController
@RequestMapping("/dct/analysis")
public class GmvAnalysisController {

    @Autowired
    private IGmvAnalysisService gmvAnalysisService;

    /**
     * 根据条件查询列表.
     * @return
     */
    @PostMapping("/gmv/list")
    public ResponseInfoVO analysisList(@RequestBody PageQueryVo pageQueryVo){
        return ResponseInfoUtil.success(gmvAnalysisService.queryGmvDataList(pageQueryVo));
    }

    /**
     * 单个PID查询.
     * @param pageQueryVo
     * @return
     */
    @PostMapping("/gmv/single/pid")
    public ResponseInfoVO analysisSinglePID(@RequestBody PageQueryVo pageQueryVo){
        return ResponseInfoUtil.success(gmvAnalysisService.queryGmvDataSinglePID(pageQueryVo));
    }



    /**
     * 单个Creator查询.
     * @param pageQueryVo
     * @return
     */
    @PostMapping("/gmv/single/creator")
    public ResponseInfoVO analysisSingleCreator(@RequestBody PageQueryVo pageQueryVo){
        return ResponseInfoUtil.success(gmvAnalysisService.queryGmvDataSingleCreator(pageQueryVo));
    }

    /**
     * 获取视频列表详情.
     * @return
     */
    @PostMapping("/gmv/video")
    public ResponseInfoVO fetchVideoList(@RequestBody JSONObject params){
        return ResponseInfoUtil.success(gmvAnalysisService.queryVideoList(params));
    }

    /**
     * 查询pid 列表参数
     * @return
     */
    @GetMapping("/gmv/pid/list/params")
    public ResponseInfoVO fetchPidListParams(){
        return ResponseInfoUtil.success(gmvAnalysisService.fetchQueryPidListParams());
    }

    /**
     * 查询creator 列表参数
     * @return
     */
    @GetMapping("/gmv/creator/list/params")
    public ResponseInfoVO fetchCreatorParams(){
        return ResponseInfoUtil.success(gmvAnalysisService.fetchQueryCreatorListParams());
    }

}
