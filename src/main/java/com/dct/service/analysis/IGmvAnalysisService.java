package com.dct.service.analysis;

import com.dct.model.vo.PageQueryVo;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:20
 */
public interface IGmvAnalysisService {
    /**
     * 根据条件查询GMV结果
     * @param pageQueryVo
     * @return
     */
    PageQueryVo queryGmvData(PageQueryVo pageQueryVo);
}
