package com.dct.controller.analysis;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 12:34
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dct.common.constant.consist.MainConstant;
import com.dct.model.vo.PageQueryVo;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.analysis.IGmvAnalysisService;
import com.dct.utils.ResponseInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * gmv分析
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 12:34 
 */
@RestController
@RequestMapping("/dct")
@Slf4j
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
    public ResponseInfoVO fetchPidListParams(@RequestParam(required = false) String creator){
        return ResponseInfoUtil.success(gmvAnalysisService.fetchQueryPidListParams(creator));
    }


    /**
     * 查询creator 列表参数
     * @return
     */
    @GetMapping("/gmv/creator/list/params")
    public ResponseInfoVO fetchCreatorParams(){
        return ResponseInfoUtil.success(gmvAnalysisService.fetchQueryCreatorListParams());
    }


    /**
     * 上传文件.
     * @param gmvFiles
     * @param vidFiles
     * @param accounts
     * @param times
     * @return
     */
    @PostMapping(value = "gmv/file/submit", headers = "content-type=multipart/form-data")
    public ResponseInfoVO saveReport(@RequestParam("gmvFile") List<MultipartFile> gmvFiles, @RequestParam("vidFile") List<MultipartFile> vidFiles,
                                     @RequestParam("account") List<String> accounts, @RequestParam("times") List<String> times,
                                     @RequestParam("country") List<String> countries) {
        try {
            if (accounts != null && accounts.size() > 0) {
                accounts.stream().forEach(a -> {
                    MultipartFile gmvFile = null;
                    MultipartFile vidFile = null;
                    MultipartFile pidFile = null;
                    MultipartFile creatorFile = null;
                    if (gmvFiles != null && gmvFiles.size() > 0) {
                        if (!gmvFiles.get(accounts.indexOf(a)).getOriginalFilename().equalsIgnoreCase(MainConstant.NULL)) {
                            gmvFile = gmvFiles.get(accounts.indexOf(a));
                        }
                    }
                    if (vidFiles != null && vidFiles.size() > 0) {
                        if (!vidFiles.get(accounts.indexOf(a)).getOriginalFilename().equalsIgnoreCase(MainConstant.NULL)) {
                            vidFile = vidFiles.get(accounts.indexOf(a));
                        }
                    }
                    String time = times.get(accounts.indexOf(a));
                    String country = countries.get(accounts.indexOf(a));
                    String account = a;
                    gmvAnalysisService.handleTkReport(gmvFile, vidFile, account, time,country);
                });
            }
        } catch (Exception e) {
            log.info(JSON.toJSONString(e));
        }
        return ResponseInfoUtil.success();
    }

    /**
     * 拉取文件.
     * @return
     */
    @GetMapping("/fetch/file")
    public ResponseInfoVO fetchGmvFile(){
        gmvAnalysisService.fetchGmvFile();
        return ResponseInfoUtil.success();
    }


    /**
     * 上传PID Creator文件.
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/picture/submit")
    public ResponseInfoVO submitPicture() throws Exception {
        JSONObject result = new JSONObject();
        try {
            gmvAnalysisService.submitPicture();
        } catch (Exception e) {
            log.error("SUBMIT APP PICTURE ERROR:{}", e.getStackTrace());
            return ResponseInfoUtil.error();
        }
        return ResponseInfoUtil.success(result);
    }

}
