package com.dct.service.analysis;

import com.alibaba.fastjson.JSONObject;
import com.dct.model.vo.PageQueryVo;
import com.dct.model.vo.VideoDetailVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:20
 */
public interface IGmvAnalysisService {
    /**
     * 根据条件查询GMV结果列表
     * @param pageQueryVo
     * @return
     */
    PageQueryVo queryGmvDataList(PageQueryVo pageQueryVo);


    /**
     * 单个PID查询
     * @param pageQueryVo
     * @return
     */
    PageQueryVo queryGmvDataSinglePID(PageQueryVo pageQueryVo);


    /**
     * 单个Creator查询
     * @param pageQueryVo
     * @return
     */
    PageQueryVo queryGmvDataSingleCreator(PageQueryVo pageQueryVo);

    /**
     * 查询具体视频播放详情.
     * @param params
     * @return
     */
    List<VideoDetailVo> queryVideoList(JSONObject params);

    /**
     * 查询PID参数
     * @return
     */
    JSONObject fetchQueryPidListParams(String creator);

    /**
     * 查询creator参数
     * @return
     */
    JSONObject fetchQueryCreatorListParams();

    /**
     * 处理csv文件
     *
     * @param gmvFile
     * @param vidFile
     * @param pidFile
     * @param creatorFile
     * @param account
     * @param time
     * @param country
     */
    void handleTkReport(MultipartFile gmvFile, MultipartFile vidFile, MultipartFile pidFile, MultipartFile creatorFile, String account, String time, String country);

    void fetchGmvFile();
}
