package com.dct.service.analysis.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:21
 */

import com.alibaba.fastjson.JSONObject;
import com.dct.config.datasource.ClickHouseConfig;
import com.dct.model.vo.PageQueryVo;
import com.dct.service.analysis.IGmvAnalysisService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:21 
 */
@Service
public class GmvAnalysisServiceImpl implements IGmvAnalysisService {





    @Override
    public PageQueryVo queryGmvData(PageQueryVo pageQueryVo) {
        JSONObject whereParam = pageQueryVo.getPageFilterVo();
        List<String> groupList = pageQueryVo.getPageGroupVo();
        List<String> metricsList = pageQueryVo.getPageMetricsVo();
        StringBuilder whereStr = new StringBuilder();
        whereParam.keySet().forEach(keyStr -> {
            List<String> filters = JSONObject.parseArray(whereParam.getJSONArray(keyStr).toJSONString(), String.class);
            if ("date".equals(keyStr)) {
                if (filters.size() == 2) {
                    whereStr.append("toDateTime(date, 'Asia/Shanghai')>='" + filters.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + filters.get(1) + "' AND ");
                }
            } else {
                if (filters.size() > 0) {
                    whereStr.append(keyStr + " in(");
                    for (int i = 0; i < filters.size(); i++) {
                        if (i == filters.size() - 1) {
                            whereStr.append("'" + filters.get(i) + "') and ");
                        } else {
                            whereStr.append("'" + filters.get(i) + "',");
                        }
                    }
                }
            }
        });

        StringBuffer queryMetricBuffer = new StringBuffer();
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(generateGroup(groupList));
        if (metricsList != null && metricsList.size() > 0 && groupList != null && groupList.size() > 0 && !sql.toString().endsWith(",")) {
            sql.append(",");
        }
        metricsList.stream().forEach(a->{
            switch (a){
                case "gmv":
                    queryMetricBuffer.append(" sum(gmv)as gmv");
                    break;
                case "orders":
                    queryMetricBuffer.append(" sum(orders)as orders");
                    break;
                case "videoViews":
                    queryMetricBuffer.append(" sum(videoViews)as videoViews");
                    break;
                case "commission":
                    queryMetricBuffer.append(" sum(commission)as commission");
            }
            queryMetricBuffer.append(a);
            if (metricsList.indexOf(a) != metricsList.size() - 1) {
                queryMetricBuffer.append(",");
            }
        });
       sql.append(queryMetricBuffer)
        .append(" FROM t_gmv_detail")
        .append(" WHERE ")
        .append(whereStr)
        .append(" GROUP BY ")
        .append(generateGroup(groupList));
        if (sql != null && groupList.contains("day")) {
            sql.append(" ORDER BY day desc");
        }
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ClickHouseConfig.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            rs = stmt.executeQuery();// 执行查询数据库的SQL语句 .
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int r = 1; r < colCount + 1; r++) {
                    map.put(rsmd.getColumnName(r), rs.getString(rsmd.getColumnName(r)));
                }
                results.add(map);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //释放资源
            ClickHouseConfig.release(conn, stmt, rs);
        }
        List<EventAdSpendAndAdIncomeReportFormVO> revenueList = new ArrayList<>();
        results.stream().forEach(a -> {
            EventAdSpendAndAdIncomeReportFormVO revenueVo = new EventAdSpendAndAdIncomeReportFormVO();
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                switch (key) {
                    case "app_package":
                        revenueVo.setApp_package(b.getValue());
                        break;
                    case "day":
                        revenueVo.setDay(b.getValue());
                        break;
                    case "hour":
                        revenueVo.setHour(b.getValue());
                        break;
                    case "user_country":
                        revenueVo.setUser_country(b.getValue());
                        break;
                    case "account_id":
                        revenueVo.setAccount_id(b.getValue());
                        break;
                    case "campaign_group_id":
                        revenueVo.setCampaign_group_id(b.getValue());
                        break;
                    case "ad_view_count":
                        revenueVo.setShowNum(b.getValue() != null ? Integer.valueOf(b.getValue()) : 0);
                        break;
                    case "revenue":
                        revenueVo.setIncome(b.getValue() != null ? Double.valueOf(b.getValue().toString()) : 0);
                        break;
                    case "user_count":
                        revenueVo.setUserNum(b.getValue() != null ? Integer.valueOf(b.getValue()) : 0);
                        break;
                    default:
                }
            });
            revenueList.add(revenueVo);
        });
        return revenueList;
    }

    private String generateGroup(List<String> groupList) {
       String groupStr = "";
        if(groupList != null && groupList.size() > 0){
           groupStr = groupList.get(0);
        }else {
            groupStr = "cid";
        }
        return groupStr;
    }

}
