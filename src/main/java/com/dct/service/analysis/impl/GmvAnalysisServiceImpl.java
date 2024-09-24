package com.dct.service.analysis.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:21
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dct.common.config.datasource.ClickHouseConfig;
import com.dct.model.dct.AccountModel;
import com.dct.model.vo.GmvDetailVo;
import com.dct.model.vo.PageQueryVo;
import com.dct.model.vo.PageVO;
import com.dct.model.vo.VideoDetailVo;
import com.dct.repo.account.AccountRepo;
import com.dct.repo.security.AdminRoleRepo;
import com.dct.repo.security.AdminUserRepo;
import com.dct.service.account.IAccountService;
import com.dct.service.analysis.IGmvAnalysisService;
import com.dct.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:21
 */
@Service
public class GmvAnalysisServiceImpl implements IGmvAnalysisService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private AdminRoleRepo adminRoleRepo;

    @Autowired
    private AdminUserRepo adminUserRepo;


    /**
     * 根据PID或者Creator分组查询.
     *
     * @param pageQueryVo
     * @return
     */
    @Override
    public PageQueryVo queryGmvDataList(PageQueryVo pageQueryVo) {
        JSONObject whereParam = pageQueryVo.getPageFilterVo();
        List<String> groupList = pageQueryVo.getPageGroupVo();
        List<String> metricsList = pageQueryVo.getPageMetricsVo();
        StringBuffer whereStr = generatePidListWhereStr(whereParam);
        StringBuffer sql = new StringBuffer();
        List<GmvDetailVo> gmvIndexList = generateGvmIndex(groupList, whereParam);
        if (groupList.contains("pid")) {
            sql.append("SELECT ");
            sql.append(generateCombinePid(groupList, metricsList));
            sql.append(" FROM (");
            sql.append(generateNormalQuery(groupList, metricsList, whereStr));
            sql.append(") as t1");
            sql.append(" left join ");
            sql.append("(");
            sql.append("SELECT productUrl,pid from product_detail");
            sql.append(") as t2 ");
            sql.append(" on t1.pid = t2.pid");
        } else {
            sql = generateNormalQuery(groupList, metricsList, whereStr);
        }
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            a.entrySet().stream().forEach(b -> {
                JSONObject jsonObject = new JSONObject();
                String key = b.getKey();
                String value = b.getValue();
                jsonObject.put(key, value);
                jsonObjectList.add(jsonObject);
            });
        });
        List<GmvDetailVo> gmvList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        if (groupList.contains("creator")) {
            //补全归属人
            List<AccountModel> accountList = accountRepo.findAll();
            Map<String, String> accountMap = accountList.stream().collect(Collectors.toMap(k -> k.getCreator(), v -> v.getManager()));
            gmvList.stream().forEach(a -> {
                if (accountMap.containsKey(a.getCreator())) {
                    a.setBelong_person(accountMap.get(a.getCreator()));
                }
            });
        }
        List<GmvDetailVo> addVideoList = new ArrayList<>();
        if (groupList.contains("day")) {
            addVideoList = generateAddVideoList(groupList, whereParam);
        }
        formatIndexAndVideoAdd(gmvList, gmvIndexList, addVideoList, groupList);
        PageVO pageVO = new PageVO();
        pageVO.setList(gmvList);
        pageQueryVo.setPageVO(pageVO);
        return pageQueryVo;
    }

    private StringBuffer generatePidListWhereStr(JSONObject whereParam) {
        StringBuffer whereStr = new StringBuffer();
        List<String> invalidWhereParamList = new ArrayList<>();
        invalidWhereParamList.add("belongPerson");
        invalidWhereParamList.add("userGroup");
        invalidWhereParamList.add("status");
        List<String> creator = generateCreator(whereParam);
        whereParam.put("creator",creator);
        whereParam.keySet().forEach(keyStr -> {
            if(!invalidWhereParamList.contains(keyStr)){
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
            }
        });
        return whereStr;
    }

    /**
     * 生成对应的creator.
     * @param params
     * @return
     */
    private List<String> generateCreator(JSONObject params) {
        List<AccountModel> accountModelList = accountService.fetchAccountList(params).getList();
        List<String> accountIdList = accountModelList.stream().map(a->a.getCreator()).collect(Collectors.toList());
        return accountIdList;
    }


    /**
     * 将gmv 排序跟新增视频数量填入到列表中.
     *
     * @param gmvList
     * @param gmvIndexList
     * @param addVideoList
     * @param groupList
     */
    private void formatIndexAndVideoAdd(List<GmvDetailVo> gmvList, List<GmvDetailVo> gmvIndexList,
                                        List<GmvDetailVo> addVideoList, List<String> groupList) {
        Map<String, Integer> indexMap = new HashMap<>();
        Map<String,Integer> addVideoMap = new HashMap<>();
        if (groupList.contains("day")) {
            gmvIndexList.stream().forEach(a -> {
                String day = a.getDate();
                String pid = a.getPid();
                String creator = a.getCreator();
                StringBuffer keyBuffer = new StringBuffer();
                keyBuffer.append(day);
                keyBuffer.append("_");
                if (StringUtils.isNotBlank(pid)) {
                    keyBuffer.append(pid);
                } else {
                    keyBuffer.append(creator);
                }
                indexMap.put(keyBuffer.toString(),a.getIndex());
            });
        }else {
            gmvIndexList.stream().forEach(a -> {
                String pid = a.getPid();
                String creator = a.getCreator();
                StringBuffer keyBuffer = new StringBuffer();
                keyBuffer.append("gmv_");
                if (StringUtils.isNotBlank(pid)) {
                    keyBuffer.append(pid);
                } else {
                    keyBuffer.append(creator);
                }
                indexMap.put(keyBuffer.toString(), a.getIndex());
            });
            addVideoList.stream().forEach(a->{
                String pid = a.getPid();
                String creator = a.getCreator();
                StringBuffer keyBuffer = new StringBuffer();
                keyBuffer.append("addVideo_");
                if (StringUtils.isNotBlank(pid)) {
                    keyBuffer.append(pid);
                } else {
                    keyBuffer.append(creator);
                }
                addVideoMap.put(keyBuffer.toString(), a.getVideos());
            });
        }
        gmvList.stream().forEach(a->{
            String pid = a.getPid();
            String creator = a.getCreator();
            String key = "";
            StringBuffer IndexBuffer = new StringBuffer();
            StringBuffer videoBuffer = new StringBuffer();
            if (StringUtils.isNotBlank(pid)) {
                key = pid;
            } else {
                key = creator;
            }
            if(groupList.contains("day")){
                String day = a.getDate();
                IndexBuffer.append(day);
            }else {
                IndexBuffer.append("gmv");
                videoBuffer.append("addVideo_");
                videoBuffer.append(key);
                Integer beforeVideoCount = addVideoMap.get(videoBuffer.toString());
                a.setAddVideos(a.getVideos() - beforeVideoCount);
            }
            IndexBuffer.append("_");
            IndexBuffer.append(key);
            Integer index = indexMap.get(IndexBuffer.toString());
            a.setIndex(index);
        });
    }

    private List<GmvDetailVo> generateAddVideoList(List<String> groupList, JSONObject whereParam) {
        List<String> timeList = JSONObject.parseArray(whereParam.getJSONArray("date").toJSONString(), String.class);
        List<String> beforeTimeList = new ArrayList<>();
        int dayCount = DateUtil.subData1toData2FormatDay(timeList.get(0), timeList.get(1));
        beforeTimeList.add(DateUtil.addTime(timeList.get(0), "D", -dayCount));
        beforeTimeList.add(timeList.get(1));
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT sum(videos)as videos,toDate(date)AS day,");
        if (groupList.contains("pid")) {
            sql.append("pid");
        } else {
            sql.append("creator");
        }
        sql.append(" FROM t_gmv_detail ");
        sql.append("toDateTime(date, 'Asia/Shanghai')>='" + beforeTimeList.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + beforeTimeList.get(1));
        if (groupList.contains("pid")) {
            sql.append(" GROUP BY pid");
        } else {
            sql.append(" GROUP BY creator");
        }
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                jsonObject.put(key, value);
                jsonObjectList.add(jsonObject);
            });
        });
        List<GmvDetailVo> gmvIndexList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        return gmvIndexList;
    }

    /**
     * 生成GVM整体排名.
     *
     * @param groupList
     * @param whereParam
     */
    private List<GmvDetailVo> generateGvmIndex(List<String> groupList, JSONObject whereParam) {
        List<String> timeList = JSONObject.parseArray(whereParam.getJSONArray("date").toJSONString(), String.class);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT sum(gmv)as gmv,toDate(date)AS day,");
        if (groupList.contains("pid")) {
            sql.append("pid");
        } else {
            sql.append("creator");
        }
        sql.append(" FROM t_gmv_detail ");
        sql.append("toDateTime(date, 'Asia/Shanghai')>='" + timeList.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + timeList.get(1) + "' AND ");
        if (groupList.contains("pid")) {
            sql.append(" GROUP BY pid,");
        } else {
            sql.append(" GROUP BY creator,");
        }
        sql.append("day");
        sql.append(" ORDER BY day,gmv desc");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index", results.indexOf(a) + 1);
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                jsonObject.put(key, value);
                jsonObjectList.add(jsonObject);
            });
        });
        List<GmvDetailVo> gmvIndexList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        return gmvIndexList;
    }

    /**
     * 查询结果
     *
     * @param sql
     * @return
     */
    private List<Map<String, String>> generateQueryResult(StringBuffer sql) {
        List<Map<String, String>> results = new ArrayList<>();
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
        return results;
    }

    /**
     * 生成查询条件SQL.
     *
     * @param whereParam
     * @return
     */
    private StringBuffer generateWhereStr(JSONObject whereParam) {
        StringBuffer whereStr = new StringBuffer();
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
        return whereStr;
    }


    /**
     * 单个PID查询
     *
     * @param pageQueryVo
     * @return
     */
    @Override
    public PageQueryVo queryGmvDataSinglePID(PageQueryVo pageQueryVo) {
        JSONObject whereParam = pageQueryVo.getPageFilterVo();
        List<String> groupList = pageQueryVo.getPageGroupVo();
        List<String> metricsList = pageQueryVo.getPageMetricsVo();
        StringBuffer whereStr = generateWhereStr(whereParam);
        StringBuffer sql = generateNormalQuery(groupList, metricsList, whereStr);
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index", results.indexOf(a) + 1);
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                jsonObject.put(key, value);
                jsonObjectList.add(jsonObject);
            });
        });
        List<GmvDetailVo> gmvList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        List<AccountModel> accountList = accountRepo.findAll();
        Map<String, String> accountMap = accountList.stream().collect(Collectors.toMap(k -> k.getCreator(), v -> v.getManager()));
        gmvList.stream().forEach(a -> {
            if (accountMap.containsKey(a.getCreator())) {
                a.setBelong_person(accountMap.get(a.getCreator()));
            }
        });
        PageVO pageVO = new PageVO();
        pageVO.setList(gmvList);
        pageQueryVo.setPageVO(pageVO);
        return pageQueryVo;
    }

    @Override
    public PageQueryVo queryGmvDataSingleCreator(PageQueryVo pageQueryVo) {
        JSONObject whereParam = pageQueryVo.getPageFilterVo();
        List<String> groupList = pageQueryVo.getPageGroupVo();
        List<String> metricsList = pageQueryVo.getPageMetricsVo();
        StringBuffer whereStr = generateWhereStr(whereParam);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append(generateCombinePid(groupList, metricsList));
        sql.append(" FROM (");
        sql.append(generateNormalQuery(groupList, metricsList, whereStr));
        sql.append(") as t1");
        sql.append(" left join ");
        sql.append("(");
        sql.append("SELECT productUrl,pid from product_detail");
        sql.append(") as t2 ");
        sql.append(" on t1.pid = t2.pid");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index", results.indexOf(a) + 1);
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                jsonObject.put(key, value);
                jsonObjectList.add(jsonObject);
            });
        });
        List<GmvDetailVo> gmvList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        PageVO pageVO = new PageVO();
        pageVO.setList(gmvList);
        pageQueryVo.setPageVO(pageVO);
        return pageQueryVo;
    }

    /**
     * 获取视频列表详细信息.
     *
     * @param params
     * @return
     */
    @Override
    public List<VideoDetailVo> queryVideoList(JSONObject params) {
        String pid = params.getString("pid");
        String creator = params.getString("creator");
        JSONArray time = params.getJSONArray("time");
        List<String> timeList = JSONArray.parseArray(JSON.toJSONString(time), String.class);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT vid,sum(video_views),sum(gmv)as gmv");
        sql.append(" FROM video_detail ");
        sql.append(" where toDateTime(date, 'Asia/Shanghai')>='" + timeList.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + timeList.get(1) + "' AND ");
        if (StringUtils.isNotBlank(pid)) {
            sql.append(" pid = '" + pid + "'");
            sql.append(" GROUP BY pid,vid");
        } else {
            sql.append(" WHERE creator = '" + creator + "'");
            sql.append(" GROUP BY creator,vid ");
        }
        sql.append(" ORDER BY gmv desc");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index", results.indexOf(a) + 1);
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                jsonObject.put(key, value);
                jsonObjectList.add(jsonObject);
            });
        });
        List<VideoDetailVo> videoList = JSONObject.parseArray(JSON.toJSONString(results), VideoDetailVo.class);
        return videoList;
    }

    @Override
    public JSONObject fetchQueryPidListParams() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT distinct(pid)as pid,distinct(level_1_category)as level_1_category,distinct(level_2_category)as level_2_category," +
                "distinct(campaign_id)as campaign_id FROM t_gmv_detail");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<String> pidList = new ArrayList<>();
        List<String> level1List = new ArrayList<>();
        List<String> level2List = new ArrayList<>();
        List<String> cidList = new ArrayList<>();
        results.stream().forEach(a->{
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                switch (key){
                    case "pid":
                        pidList.add(value);
                        break;
                    case "level_1_category":
                        level1List.add(value);
                        break;
                    case "level_2_category":
                        level2List.add(value);
                        break;
                    case "campaign_id":
                        cidList.add(value);
                        break;
                    default:
                }
            });
        });
        JSONObject params = new JSONObject();
        params.put("pid",pidList);
        params.put("level_1_category",level1List);
        params.put("level_2_category",level2List);
        params.put("cid",cidList);
        return params;
    }

    @Override
    public JSONObject fetchQueryCreatorListParams() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT distinct(creator)creator as creator FROM t_gmv_detail");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<String> creatorList = new ArrayList<>();
        results.stream().forEach(a->{
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                switch (key){
                    case "craetor":
                        creatorList.add(value);
                        break;
                    default:
                }
            });
        });
        JSONObject params = new JSONObject();
        params.put("creator",creatorList);
        List<String> userList = adminUserRepo.findAll().stream().map(a->a.getUsername()).collect(Collectors.toList());
        List<String> userGroupList = adminRoleRepo.findAllRoleName();
        params.put("user",userList);
        params.put("userGroup",userGroupList);
        return params;
    }


    private StringBuffer generateCombinePid(List<String> groupList, List<String> metricsList) {
        StringBuffer sql = new StringBuffer();
        groupList.stream().forEach(a -> {
            sql.append("t1.");
            sql.append(a);
            sql.append(",");
        });
        metricsList.stream().forEach(a -> {
            sql.append("t1.");
            sql.append(a);
            if (metricsList.indexOf(a) != metricsList.size() - 1) {
                sql.append(",");
            }
        });
        sql.append("t2.productUrl");
        return sql;
    }

    private StringBuffer generateNormalQuery(List<String> groupList, List<String> metricsList, StringBuffer whereStr) {
        StringBuffer sql = new StringBuffer("SELECT ");
        sql.append(generateGroup(groupList));
        StringBuffer queryMetricBuffer = new StringBuffer();
        metricsList.stream().forEach(a -> {
            switch (a) {
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
                    break;
                case "videos":
                    queryMetricBuffer.append(" sum(videos)as videos");
                    break;
                case "date":
                    queryMetricBuffer.append(" toDate(date)AS day");
                    break;
                default:
                    queryMetricBuffer.append(a);
            }
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
            sql.append(" ORDER BY day,gvm desc");
        } else {
            sql.append(" ORDER BY gvm desc");
        }
        return sql;
    }

    private StringBuffer generateGroup(List<String> groupList) {
        StringBuffer sql = new StringBuffer();
        groupList.stream().forEach(a -> {
            sql.append(a);
            if (groupList.indexOf(a) != groupList.size() - 1) {
                sql.append(",");
            }
        });
        return sql;
    }

}
