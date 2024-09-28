package com.dct.service.analysis.impl;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:21
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.clickhouse.client.internal.google.common.util.concurrent.ThreadFactoryBuilder;
import com.dct.common.config.datasource.ClickHouseConfig;
import com.dct.common.constant.consist.MainConstant;
import com.dct.common.constant.enums.NumberEnum;
import com.dct.model.ck.GmvDetailModel;
import com.dct.model.ck.VideoDetailModel;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/04 14:21
 */
@Service
@Slf4j
public class GmvAnalysisServiceImpl implements IGmvAnalysisService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private AdminRoleRepo adminRoleRepo;

    @Autowired
    private AdminUserRepo adminUserRepo;



    @Value("${spring.datasource.druid.ck.url}")
    private String clickhouseUrl;


    @Value("${spring.datasource.druid.ck.username}")
    private String clickHouseUserName;


    @Value("${spring.datasource.druid.ck.password}")
    private String clickHousePassword;



    Map<String,Integer> gmvIndexMap = new HashMap<>();

    /**
     * GMV 数据列.
     */
    Map<String, Integer> gmvDataIndex = new HashMap<>(0);

    /**
     * VID 数据列.
     */
    Map<String, Integer> vidDataIndex = new HashMap<>(0);


    /**
     * pid 数据列.
     */
    Map<String, Integer> pidDataIndex = new HashMap<>(0);


    /**
     * creator 数据列.
     */
    Map<String, Integer> creatorDataIndex = new HashMap<>(0);


    @Resource
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;



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
        if(groupList.contains("creator")){
            resetCreatorWhereParam(whereParam);
        }
        StringBuffer whereStr = generateListWhereStr(whereParam);
        StringBuffer sql = new StringBuffer();
        List<GmvDetailVo> gmvIndexList = generateGvmIndex(groupList, whereParam);
        if (groupList.contains("product_id")) {
//            sql.append("SELECT ");
//            sql.append(generateCombine(groupList, metricsList,"product_id"));
//            sql.append(" FROM (");
//            sql.append(generateNormalQuery(groupList, metricsList, whereStr));
//            sql.append(") as t1");
//            sql.append(" left join ");
//            sql.append("(");
//            sql.append("SELECT picture,product_id from product_detail");
//            sql.append(") as t2 ");
//            sql.append(" on t1.product_id = t2.product_id");
            sql.append(generateNormalQuery(groupList, metricsList, whereStr));
        } else {
            sql.append(generateNormalQuery(groupList, metricsList, whereStr));
//            sql.append("SELECT ");
//            sql.append(generateCombine(groupList, metricsList,"creator"));
//            sql.append(" FROM (");
//            sql.append(generateNormalQuery(groupList, metricsList, whereStr));
//            sql.append(") as t1");
//            sql.append(" left join ");
//            sql.append("(");
//            sql.append("SELECT profile_picture,creator from creator_detail");
//            sql.append(") as t2 ");
//            sql.append(" on t1.creator = t2.creator");
        }
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                if(key.equals("day")){
                    jsonObject.put("date", value);
                }else {
                    jsonObject.put(key, value);
                }
                if(!groupList.contains("day")){
                    jsonObject.put("date",MainConstant.TOTAL);
                }
            });
            jsonObjectList.add(jsonObject);

        });
        List<GmvDetailVo> gmvList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        if (groupList.contains("creator")) {
            //补全归属人
            List<AccountModel> accountList = accountRepo.findAll();
            if(accountList != null && accountList.size() > 0){
                Map<String, String> accountMap = accountList.stream()
                        .filter(accountModel -> StringUtils.isNotBlank(accountModel.getCreator()) && StringUtils.isNotBlank(accountModel.getBelongPerson()))
                        .collect(Collectors.toMap(k -> k.getCreator(), v -> v.getBelongPerson()));
                gmvList.stream().forEach(a -> {
                    if (accountMap.containsKey(a.getCreator())) {
                        a.setBelong_person(accountMap.get(a.getCreator()));
                    }
                });
            }
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

    private void resetCreatorWhereParam(JSONObject whereParam) {
        List<String> userList = new ArrayList<>();
        List<String> userGroupList = new ArrayList<>();
        List<Integer> statusList = new ArrayList<>();
        List<String> keyList = whereParam.keySet().stream().collect(Collectors.toList());
        for(String keyStr : keyList){
            if(keyStr.equals("user")){
                userList = JSONObject.parseArray(whereParam.getJSONArray(keyStr).toJSONString(), String.class);
            }
            if(keyStr.equals("userGroup")){
                userGroupList = JSONObject.parseArray(whereParam.getJSONArray(keyStr).toJSONString(), String.class);
            }
            if(keyStr.equals("status")){
                statusList = JSONObject.parseArray(whereParam.getJSONArray(keyStr).toJSONString(), Integer.class);
            }
        }
        if(userList.size()>0 || userGroupList.size() >0 || statusList.size() >0 ){
            JSONObject params = new JSONObject();
            params.put("belongPerson",userList);
            params.put("userGroup",userGroupList);
            params.put("status",statusList);

            List<AccountModel> creatorList = accountService.fetchAccountList(params).getList();
            List<String> newCreatorList = creatorList.stream().map(a->a.getCreator()).collect(Collectors.toList());
            List<String> creatorParamList =  JSONObject.parseArray(whereParam.getJSONArray("creator").toJSONString(), String.class);
            creatorParamList.addAll(newCreatorList);
            whereParam.put("creator",creatorParamList);
        }
    }

    private StringBuffer generateListWhereStr(JSONObject whereParam) {
        StringBuffer whereStr = new StringBuffer();
        List<String> invalidWhereParamList = new ArrayList<>();
        invalidWhereParamList.add("user");
        invalidWhereParamList.add("userGroup");
        invalidWhereParamList.add("status");
//        whereParam.put("creator",creator);
        whereParam.keySet().forEach(keyStr -> {
            if(!invalidWhereParamList.contains(keyStr)){
                List<String> filters = JSONObject.parseArray(whereParam.getJSONArray(keyStr).toJSONString(), String.class);
                if ("time".equals(keyStr)) {
                    if (filters.size() == 2) {
                        whereStr.append("toDateTime(date, 'Asia/Shanghai')>='" + filters.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + filters.get(1) + "'");
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
        gmvIndexList.stream().forEach(a -> {
            String pid = a.getProduct_id();
            String creator = a.getCreator();
            StringBuffer keyBuffer = new StringBuffer();
            keyBuffer.append(a.getDate() + "_");
            if (StringUtils.isNotBlank(pid)) {
                keyBuffer.append(pid);
            } else {
                keyBuffer.append(creator);
            }
            indexMap.put(keyBuffer.toString(), a.getIndex());
        });
        addVideoList.stream().forEach(a->{
            String pid = a.getProduct_id();
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
        gmvList.stream().forEach(a->{
            String pid = a.getProduct_id();
            String creator = a.getCreator();
            String key = "";
            StringBuffer IndexBuffer = new StringBuffer();
            StringBuffer videoBuffer = new StringBuffer();
            if (StringUtils.isNotBlank(pid)) {
                key = pid;
            } else {
                key = creator;
            }
            String day = a.getDate();
            videoBuffer.append(day + "_");
            videoBuffer.append(key);
            Integer beforeVideoCount = addVideoMap.get(videoBuffer.toString());
            a.setAddVideos(a.getVideos() - (beforeVideoCount == null ? 0 : beforeVideoCount));
            IndexBuffer.append(day + "_");
            IndexBuffer.append(key);
            Integer index = indexMap.get(IndexBuffer.toString());
            a.setIndex(index);
        });
    }

    private List<GmvDetailVo> generateAddVideoList(List<String> groupList, JSONObject whereParam) {
        List<String> timeList = JSONObject.parseArray(whereParam.getJSONArray("time").toJSONString(), String.class);
        List<String> beforeTimeList = new ArrayList<>();
        int dayCount = DateUtil.subData1toData2FormatDay(timeList.get(0), timeList.get(1));
        if(dayCount == 0){
            dayCount = 1;
        }
        beforeTimeList.add(DateUtil.addTime(timeList.get(0), "D", -dayCount));
        beforeTimeList.add(timeList.get(1));
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT sum(videos)as videos,toDate(date)AS day,");
        if (groupList.contains("product_id")) {
            sql.append("product_id");
        } else {
            sql.append("creator");
        }
        sql.append(" FROM gmv_detail ");
        sql.append("toDateTime(date, 'Asia/Shanghai')>='" + beforeTimeList.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + beforeTimeList.get(1));
        if (groupList.contains("product_id")) {
            sql.append(" GROUP BY product_id");
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
            });
            jsonObjectList.add(jsonObject);
        });
        List<GmvDetailVo> videoAddList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        return videoAddList;
    }

    /**
     * 生成GVM整体排名.
     *
     * @param groupList
     * @param whereParam
     */
    private List<GmvDetailVo> generateGvmIndex(List<String> groupList, JSONObject whereParam) {
        List<String> timeList = JSONObject.parseArray(whereParam.getJSONArray("time").toJSONString(), String.class);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT sum(gmv)as gmv,toDate(date)AS day,");
        if (groupList.contains("product_id")) {
            sql.append("product_id");
        } else {
            sql.append("creator");
        }
        sql.append(" FROM gmv_detail where ");
        sql.append("toDateTime(date, 'Asia/Shanghai')>='" + timeList.get(0) + "' AND toDateTime(date, 'Asia/Shanghai')<='" + timeList.get(1) + "'");
        if (groupList.contains("product_id")) {
            sql.append(" GROUP BY product_id,");
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
                if(key.equals("day")){
                    jsonObject.put("date",value);
                }else {
                    jsonObject.put(key, value);
                }
                if(!groupList.contains("day")){
                    jsonObject.put("date",MainConstant.TOTAL);
                }
            });
            jsonObjectList.add(jsonObject);
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
            if ("time".equals(keyStr)) {
                if (filters.size() == 2) {
                    whereStr.append("toDateTime(date, 'Asia/Shanghai')>='" + filters.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + filters.get(1) + "' ");
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
//        Map<String,Integer> gmvIndexMap= generatePerGvmIndex(groupList, whereParam);
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                if(key.equals("day")){
                    jsonObject.put("date",value);
                }else {
                    jsonObject.put(key, value);
                }
            });
            jsonObjectList.add(jsonObject);
        });
        List<GmvDetailVo> gmvList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
        List<AccountModel> accountList = accountRepo.findAll();
        Map<String, String> accountMap = accountList.stream().collect(Collectors.toMap(k -> k.getCreator(), v -> v.getManager()));
        gmvList.stream().forEach(a -> {
            if (accountMap.containsKey(a.getCreator())) {
                a.setBelong_person(accountMap.get(a.getCreator()));
            }
        });
//        gmvList.stream().forEach(a->{
//            String key = a.getDate() + "_" + a.getCreator() + "_" + a.getProduct_id();
//            Integer index = gmvIndexMap.get(key) + 1;
//            a.setIndex(index);
//        });
        PageVO pageVO = new PageVO();
        pageVO.setList(gmvList);
        pageQueryVo.setPageVO(pageVO);
        return pageQueryVo;
    }

    private Map<String,Integer> generatePerGvmIndex(List<String> groupList, JSONObject whereParam) {
        Map<String,Integer> gmvIndexMap = new HashMap<>();
        List<String> timeList = JSONObject.parseArray(whereParam.getJSONArray("time").toJSONString(), String.class);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT toDate(date)AS day,creator, product_id,sum(gmv)as gmv");
        sql.append(" FROM gmv_detail where ");
        sql.append("toDateTime(date, 'Asia/Shanghai')>='" + timeList.get(0) + "' AND toDateTime(date, 'Asia/Shanghai')<='" + timeList.get(1) + "'");
        sql.append(" GROUP BY product_id,creator,day");
        sql.append(" ORDER BY gmv desc");
        List<Map<String, String>> results = generateQueryResult(sql);
        for(Map<String,String> map : results){


            taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    int index = results.indexOf(map) + 1;
                    generateData(map,index,gmvIndexMap);

                }
            });

        }
        return gmvIndexMap;
    }

    public void generateData(Map<String, String> results, int index, Map<String, Integer> gmvDataIndex) {
        String key = "";
        StringBuffer buffer = new StringBuffer();
        results.entrySet().stream().forEach(b -> {
            if(!b.getKey().equals("gmv")){
                buffer.append(b.getValue());
                buffer.append("##");
            }
        });
        key = buffer.toString().substring(0,buffer.toString().length() -2);
        gmvDataIndex.put(key, index);
    }

    @Override
    public PageQueryVo queryGmvDataSingleCreator(PageQueryVo pageQueryVo) {
        JSONObject whereParam = pageQueryVo.getPageFilterVo();
        List<String> groupList = pageQueryVo.getPageGroupVo();
        List<String> metricsList = pageQueryVo.getPageMetricsVo();
        StringBuffer whereStr = generateWhereStr(whereParam);
//        Map<String,Integer> gmvIndexSortMap = generatePerGvmIndex(groupList, whereParam);
        List<String> creatorList = JSONObject.parseArray(whereParam.getJSONArray("creator").toJSONString(), String.class);
        List<String> pidList = JSONObject.parseArray(whereParam.getJSONArray("product_id").toJSONString(), String.class);
        String creator = "";
        String pid = "";
        if(creatorList.size() > 0){
            creator = creatorList.get(0);
        }
        if(pidList.size() > 0){
            pid  = pidList.get(0);
        }
        StringBuffer sql = new StringBuffer();
//        sql.append("SELECT ");
//        sql.append(generateCombine(groupList, metricsList,"creator"));
//        sql.append(" FROM (");
//        sql.append(generateNormalQuery(groupList, metricsList, whereStr));
//        sql.append(") as t1");
//        sql.append(" left join ");
//        sql.append("(");
//        sql.append("SELECT picture,product_id from product_detail");
//        sql.append(") as t2 ");
//        sql.append(" on t1.product_id = t2.product_id");

        sql.append(generateNormalQuery(groupList, metricsList, whereStr));
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                if(key.equals("day")){
                    jsonObject.put("date",value);
                }else {
                    jsonObject.put(key, value);
                }
            });
            jsonObjectList.add(jsonObject);
        });
        List<GmvDetailVo> gmvList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), GmvDetailVo.class);
//        for(GmvDetailVo gmvDetailVo : gmvList){
//            String finalCreator = StringUtils.isNotBlank(gmvDetailVo.getCreator()) ? gmvDetailVo.getCreator() : creator;
//            String finalPid = StringUtils.isNotBlank(gmvDetailVo.getProduct_id()) ? gmvDetailVo.getProduct_id() : pid;
//            String key =   finalCreator + "##" + finalPid + "##" + gmvDetailVo.getDate() ;
//            Integer index = gmvIndexSortMap.containsKey(key) ? gmvIndexMap.get(key) + 1 : 0;
//            gmvDetailVo.setIndex(index);
//        }

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
        List<VideoDetailVo> videoGmvIndexList = generateVideoIndex(pid,creator,timeList);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT vid,url,sum(video_views)as video_views,sum(gmv)as gmv,toDate(date)AS day");
        sql.append(" FROM video_detail ");
        sql.append(" where toDateTime(date, 'Asia/Shanghai')>='" + timeList.get(0) + "' and toDateTime(date, 'Asia/Shanghai')<='" + timeList.get(1) + "' AND ");
        if(StringUtils.isNotBlank(creator) && StringUtils.isNotBlank(pid)){
            sql.append(" product_id = '" + pid + "'");
            sql.append(" AND ");
            sql.append(" creator = '" + creator + "'");
        } else {
            if (StringUtils.isNotBlank(pid)) {
                sql.append(" product_id = '" + pid + "'");
            }
            if(StringUtils.isNotBlank(creator)){
                sql.append(" creator = '" + creator + "'");
            }
        }
        sql.append(" GROUP BY vid,url,day");
        sql.append(" ORDER BY gmv desc");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                if(key.equals("day")){
                    jsonObject.put("time", value);
                }else {
                    jsonObject.put(key, value);
                }
            });
            jsonObjectList.add(jsonObject);
        });
        List<VideoDetailVo> videoList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), VideoDetailVo.class);
        Map<String,Integer> indexMap = videoGmvIndexList.stream().collect(Collectors.toMap(k->k.getVid(),v->v.getIndex()));
        for(VideoDetailVo videoDetailVo:videoList){
            videoDetailVo.setIndex(indexMap.get(videoDetailVo.getVid()));
        }
        return videoList;
    }

    private List<VideoDetailVo> generateVideoIndex(String pid, String creator, List<String> timeList) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT sum(gmv)as gmv,toDate(date)AS day,vid");
        sql.append(" FROM video_detail where ");
        sql.append("toDateTime(date, 'Asia/Shanghai')>='" + timeList.get(0) + "' AND toDateTime(date, 'Asia/Shanghai')<='" + timeList.get(1) + "'");
        sql.append(" GROUP BY vid,day");
        sql.append(" ORDER BY gmv desc");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        results.stream().forEach(a -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index", results.indexOf(a) + 1);
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                if(key.equals("day")){
                    jsonObject.put("date",value);
                }else {
                    jsonObject.put(key, value);
                }
            });
            jsonObjectList.add(jsonObject);
        });
        List<VideoDetailVo> gmvIndexList = JSONObject.parseArray(JSON.toJSONString(jsonObjectList), VideoDetailVo.class);
        return gmvIndexList;
    }

    @Override
    public JSONObject fetchQueryPidListParams(String creator) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT product_id,level_1_category,level_2_category,campaign_id FROM gmv_detail group by product_id,level_1_category,level_2_category,campaign_id");
        if(!(StringUtils.isBlank(creator) || creator.equals("undefined"))){
            sql.append(" where creator = '" + creator + "'");
        }
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
                    case "product_id":
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
        params.put("pid",pidList.stream().distinct());
        params.put("level_1_category",level1List.stream().distinct());
        params.put("level_2_category",level2List.stream().distinct());
        params.put("cid",cidList.stream().distinct());
        return params;
    }

    @Override
    public JSONObject fetchQueryCreatorListParams() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT creator FROM gmv_detail GROUP BY creator ");
        List<Map<String, String>> results = generateQueryResult(sql);
        List<String> creatorList = new ArrayList<>();
        results.stream().forEach(a->{
            a.entrySet().stream().forEach(b -> {
                String key = b.getKey();
                String value = b.getValue();
                switch (key){
                    case "creator":
                        creatorList.add(value);
                        break;
                    default:
                }
            });
        });
        JSONObject params = new JSONObject();
        params.put("creator",creatorList.stream().filter(a->StringUtils.isNotBlank(a)));
        List<String> userList = adminUserRepo.findAll().stream().map(a->a.getUsername()).collect(Collectors.toList());
        List<String> userGroupList = adminRoleRepo.findAllRoleName();
        params.put("user",userList);
        params.put("userGroup",userGroupList);
        return params;
    }

    @Override
    public void handleTkReport(MultipartFile gmvFile, MultipartFile vidFile, MultipartFile pidFile, MultipartFile creatorFile, String account, String time, String country) {
        if(gmvFile != null){
            handleGmvFile(gmvFile,account,time,country);
        }
        if(vidFile != null){
            handleVidFile(vidFile,account,time,country);
        }
        if(pidFile != null){
            handlePidFile(pidFile,account,time,country);
        }
        if(creatorFile != null){
            handleCreatorFile(creatorFile,account,time,country);
        }
    }

    public void deleteFile(File excelFile) {
        if (excelFile.exists()) {
            excelFile.delete();
        }
    }

    private void handleCreatorFile(MultipartFile creatorFile, String account, String time, String country) {
        try {
            File tempFile = multipartFileToFile(creatorFile);
            importCsvFile(tempFile, account, time,"creator", country);
            deleteFile(tempFile);
        } catch (Exception e) {
            log.error("HANDLE SUBMIT FILE:{}, ERROR:{}", account, e);
        }
    }

    private void handlePidFile(MultipartFile pidFile, String account, String time, String country) {
        try {
            File tempFile = multipartFileToFile(pidFile);
            importCsvFile(tempFile, account, time,"pid", country);
            deleteFile(tempFile);
        } catch (Exception e) {
            log.error("HANDLE SUBMIT FILE:{}, ERROR:{}", account, e);
        }
    }

    private void handleVidFile(MultipartFile vidFile, String account, String time, String country) {
        try {
            File tempFile = multipartFileToFile(vidFile);
            importCsvFile(tempFile, account, time,"vid", country);
            deleteFile(tempFile);
        } catch (Exception e) {
            log.error("HANDLE SUBMIT FILE:{}, ERROR:{}", account, e);
        }
    }

    private void handleGmvFile(MultipartFile gmvFile, String account, String time, String country) {
        try {
            File tempFile = multipartFileToFile(gmvFile);
            importCsvFile(tempFile, account, time,"gmv",country);
            deleteFile(tempFile);
        } catch (Exception e) {
            log.error("HANDLE SUBMIT FILE:{}, ERROR:{}", account, e);
        }
    }

    /**
     * 处理
     *
     * @param file
     * @param account
     * @param time
     * @param type
     * @param country
     */
    private void importCsvFile(File file, String account, String time, String type, String country) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            switch (type){
                case "gmv":
                    generateGmvIndex(reader);
                    saveGmvData(reader,account,time,country);
                    break;
                case "vid":
                    generateVidIndex(reader);
                    saveVidData(reader,account,time);
                    break;
                case "pid":
                    generatePidIndex(reader,account,time);
                    savePidData(reader,account,time);
                    break;
                case "creator":
                    generateCreatorIndex(reader,account,time);
                    saveCreatorData(reader,account,time);
                    break;
                default:
            }
        }catch (Exception e){

        }

    }

    private void saveCreatorData(BufferedReader reader, String account, String time) {
    }

    private void generateCreatorIndex(BufferedReader reader, String account, String time) {

    }

    private void savePidData(BufferedReader reader, String account, String time) {

    }

    private void generatePidIndex(BufferedReader reader, String account, String time) {

    }

    private void saveVidData(BufferedReader reader, String account, String time) {
        List<VideoDetailModel> videoDetailModels = new ArrayList<>();
        try {
            int index = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                boolean validate = true;
                if (validate) {
                    VideoDetailModel model = new VideoDetailModel();
                    String[] item = line.split("#");
                    String creator = item[vidDataIndex.get(MainConstant.CREATOR)];
                    String product_id = item[vidDataIndex.get(MainConstant.PID)];
                    String vid = item[vidDataIndex.get(MainConstant.VID)];
                    String date = item[vidDataIndex.get(MainConstant.DATE)];
                    double gmv = Double.valueOf(generateFormatValue(item[vidDataIndex.get(MainConstant.GMV)]));
                    Integer video_views = Integer.valueOf(generateFormatValue(item[vidDataIndex.get(MainConstant.VIDEO_VIEWS)]));
                    double commission = Double.valueOf(generateFormatValue(item[vidDataIndex.get(MainConstant.COMMISSION)]));
                    model.setDate(StringUtils.isNotBlank(date) ? DateUtil.parseTimeByDayFormat(date.substring(0,10) + " 10:00:00") : DateUtil.parseDayByDayFormat(time));
                    model.setAccount(account);
                    model.setCreator(creator);
                    model.setProduct_id(product_id);
                    model.setGmv(gmv);
                    model.setVid(vid);
                    model.setVideo_views(video_views);
                    model.setCommission(commission);
                    String url = "http://www.tiktok.com/@handle/video/" + vid;
                    model.setUrl(url);
                    videoDetailModels.add(model);
                    index++;
                } else {
                    break;
                }
            }
            List<JSONObject> dataList = new ArrayList<>();
            videoDetailModels.stream().forEach(a->{
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(a);
                dataList.add(jsonObject);
            });
            insertClickHouse(dataList,"vid");
        } catch (Exception e) {
            log.error( "  GENERATE VIDEO DATA ERROR:{}", e.getMessage());
        }
    }

    private void generateVidIndex(BufferedReader reader) {
        try {
            String line = null;
            int indexLine = 0;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split("#");
                if (indexLine == 0) {
                    for (int i = 0; i < item.length; i++) {
                        switch (i) {
                            case 0:
                                vidDataIndex.put(MainConstant.DATE, i);
                                break;
                            case 4:
                                vidDataIndex.put(MainConstant.CREATOR, i);
                                break;
                            case 5:
                                vidDataIndex.put(MainConstant.VID, i);
                                break;
                            case 7:
                                vidDataIndex.put(MainConstant.PID, i);
                                break;
                            case 9:
                                vidDataIndex.put(MainConstant.GMV, i);
                                break;
                            case 11:
                                vidDataIndex.put(MainConstant.COMMISSION, i);
                                break;
                            case 14:
                                vidDataIndex.put(MainConstant.VIDEO_VIEWS, i);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                }

            }
        } catch (Exception e) {
            log.error("GET VIDEO DATA INDEX ERROR:{}", e.getMessage());
        }

    }

    private void saveGmvData(BufferedReader reader, String account, String time, String country) {
        List<GmvDetailModel> gmvDetailModels = new ArrayList<>();
        try {
            int index = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                boolean validate = true;
                if (validate) {
                    GmvDetailModel model = new GmvDetailModel();
                    String[] item = line.split("#");
                    String date =  item[gmvDataIndex.get(MainConstant.DATE)];
                    String campaign_id = item[gmvDataIndex.get(MainConstant.CAMPAIGN_ID)];
                    String campaign_name = item[gmvDataIndex.get(MainConstant.CAMPAIGN_NAME)];
                    String creator = item[gmvDataIndex.get(MainConstant.CREATOR)];
                    String product_id = item[gmvDataIndex.get(MainConstant.PID)];
                    String product_name =  item[gmvDataIndex.get(MainConstant.PRODUCT_NAME)];
                    String level_1_category = item[gmvDataIndex.get(MainConstant.LEVEL_1_CATEGORY)];
                    String level_2_category = item[gmvDataIndex.get(MainConstant.LEVEL_2_CATEGORY)];
                    double gmv = Double.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.GMV)]));
                    Integer order = Integer.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.ORDER)]));
                    Integer video = Integer.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.VIDEOS)]));
                    Integer video_views = Integer.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.VIDEO_VIEWS)]));
                    double creatorCommission = Double.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.CREATOR_COMMISSION)]));
                    double partnerCommission = Double.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.PARTNER_COMMISSION)]));
                    model.setCampaign_id(campaign_id);
                    model.setCampaign_name(campaign_name);
                    model.setCountry(country);
                    model.setDate(StringUtils.isNotBlank(date) ? DateUtil.parseTimeByDayFormat(date.substring(0,10) + " 10:00:00") : DateUtil.parseDayByDayFormat(time));
                    model.setAccount(account);
                    model.setCreator(creator);
                    model.setProduct_id(product_id);
                    model.setProduct_name(product_name);
                    model.setLevel_1_category(level_1_category);
                    model.setLevel_2_category(level_2_category);
                    model.setGmv(gmv);
                    model.setOrders(order);
                    model.setVideos(video);
                    model.setVideo_views(video_views);
                    model.setCreator_commission(creatorCommission);
                    model.setPartner_commission(partnerCommission);
                    gmvDetailModels.add(model);
                    index++;
                } else {
                    break;
                }
            }
            List<JSONObject> dataList = new ArrayList<>();
            gmvDetailModels.stream().forEach(a->{
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(a);
                dataList.add(jsonObject);
            });
            insertClickHouse(dataList,"gmv");
        } catch (Exception e) {
            log.error( "  GENERATE GMV DATA ERROR:{}", e.getMessage());
        }
    }

    public void insertClickHouse(List<JSONObject> dataList,String type){
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            Class.forName("com.clickhouse.jdbc.ClickHouseDriver");
            connection = DriverManager.getConnection(clickhouseUrl, clickHouseUserName, clickHousePassword);
            connection.setAutoCommit(false);
            String sql = "";
            switch (type){
                case "gmv":
                    sql = "INSERT INTO gmv_detail (id,date,account,country,creator,campaign_id,campaign_name,level_1_category,level_2_category,product_id,product_name,gmv,orders,video_views,videos,creator_commission,partner_commission) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    break;
                case "vid":
                    sql = "INSERT INTO video_detail (id, account,creator,commission,gmv,product_id,url,vid,video_views,date) VALUES (?,?,?,?,?,?,?,?,?,?)";
                    break;
                case "pid":
                    sql = "";
                    break;
                case "creator":
                    sql = "INSERT INTO your_table (column1, column2) VALUES (?, ?)";
                    break;
                default:
            }
            statement = connection.prepareStatement(sql);
            switch (type){
                case "gmv":
                    List<GmvDetailModel> gmvDetailModels = JSONObject.parseArray(JSON.toJSONString(dataList), GmvDetailModel.class);
                    setGmvStatement(statement,gmvDetailModels);
                    break;
                case "vid":
                    List<VideoDetailModel> videoDetailModels = JSONObject.parseArray(JSON.toJSONString(dataList),VideoDetailModel.class);
                    setVidStatement(statement,videoDetailModels);
                    break;
                default:
            }
            int[] updateCounts = statement.executeBatch();
            connection.commit();
            System.out.println("Batch insert complete. Affected rows: " + updateCounts.length);

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setVidStatement(PreparedStatement statement, List<VideoDetailModel> videoDetailModels) {
        videoDetailModels.stream().forEach(a->{
            try {
                statement.setString(1,String.valueOf(UUID.randomUUID()));
                statement.setString(2,a.getAccount());
                statement.setString(3,a.getCreator());
                statement.setDouble(4,a.getCommission());
                statement.setDouble(5,a.getGmv());
                statement.setString(6,a.getProduct_id());
                statement.setString(7,a.getUrl());
                statement.setString(8,a.getVid());
                statement.setInt(9,a.getVideo_views());
                statement.setLong(10,a.getDate());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setGmvStatement(PreparedStatement statement, List<GmvDetailModel> gmvDetailModels) {
        gmvDetailModels.stream().forEach(a->{
            try {
                statement.setString(1, String.valueOf(UUID.randomUUID()));
                statement.setLong(2,a.getDate());
                statement.setString(3,a.getAccount());
                statement.setString(4,a.getCountry());
                statement.setString(5,a.getCreator());
                statement.setString(6,a.getCampaign_id());
                statement.setString(7,a.getCampaign_name());
                statement.setString(8,a.getLevel_1_category());
                statement.setString(9,a.getLevel_2_category());
                statement.setString(10,a.getProduct_id());
                statement.setString(11,a.getProduct_name());
                statement.setDouble(12,a.getGmv());
                statement.setInt(13,a.getOrders());
                statement.setInt(14,a.getVideo_views());
                statement.setInt(15,a.getVideos());
                statement.setDouble(16,a.getCreator_commission());
                statement.setDouble(17,a.getPartner_commission());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /**
     * format num.
     *
     * @param value
     * @return
     */
    private String generateFormatValue(String value) {
        if(value.contains("$")){
            value = value.replaceAll("\\$","");
        }
        value = StringUtils.isBlank(value) || value.equalsIgnoreCase(MainConstant.MID_LINE) ? "0" : value;
        return value;
    }

    private void generateGmvIndex(BufferedReader reader) {
        try {
            String line = null;
            int indexLine = 0;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split("#");
                if (indexLine == 0) {
                    for (int i = 0; i < item.length; i++) {
                        switch (i) {
                            case 0:
                                gmvDataIndex.put(MainConstant.DATE, i);
                                break;
                            case 1:
                                gmvDataIndex.put(MainConstant.CAMPAIGN_ID, i);
                                break;
                            case 2:
                                gmvDataIndex.put(MainConstant.CAMPAIGN_NAME, i);
                                break;
                            case 4:
                                gmvDataIndex.put(MainConstant.CREATOR, i);
                                break;
                            case 5:
                                gmvDataIndex.put(MainConstant.PID, i);
                                break;
                            case 6:
                                gmvDataIndex.put(MainConstant.PRODUCT_NAME, i);
                                break;
                            case 7:
                                gmvDataIndex.put(MainConstant.LEVEL_1_CATEGORY, i);
                                break;
                            case 8:
                                gmvDataIndex.put(MainConstant.LEVEL_2_CATEGORY, i);
                                break;
                            case 9:
                                gmvDataIndex.put(MainConstant.GMV, i);
                                break;
                            case 12:
                                gmvDataIndex.put(MainConstant.ORDER, i);
                                break;
                            case 17:
                                gmvDataIndex.put(MainConstant.VIDEO_VIEWS, i);
                                break;
                            case 20:
                                gmvDataIndex.put(MainConstant.VIDEOS, i);
                                break;
                            case 23:
                                gmvDataIndex.put(MainConstant.PARTNER_COMMISSION, i);
                                break;
                            case 25:
                                gmvDataIndex.put(MainConstant.CREATOR_COMMISSION, i);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                }
                indexLine++;
            }
        } catch (Exception e) {
            log.error("GET GMV DATA INDEX ERRPR:{}", e.getMessage());
        }
    }


    /**
     * multipartFileToFile.
     *
     * @param multipartFile
     * @return
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        File file = null;
        //判断是否为null
        if (multipartFile == null) {
            return file;
        }
        InputStream ins = null;
        OutputStream os = null;
        try {
            ins = multipartFile.getInputStream();
            file = new File(multipartFile.getOriginalFilename());
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[NumberEnum.EIGHT_ONE_NINE_TWO.getNum()];
            while ((bytesRead = ins.read(buffer, 0, NumberEnum.EIGHT_ONE_NINE_TWO.getNum())) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    private StringBuffer generateCombine(List<String> groupList, List<String> metricsList,String type) {
        StringBuffer sql = new StringBuffer();
        groupList.stream().forEach(a -> {
            sql.append("t1.");
            sql.append(a);
            sql.append(",");
        });
        metricsList.stream().forEach(a -> {
            if(!a.equals("date")){
                sql.append("t1.");
                sql.append(a);
                if (metricsList.indexOf(a) != metricsList.size() - 1) {
                    sql.append(",");
                }
            }
        });
        if(type.equals("product_id")){
            sql.append(",t2.picture");
        }else {
            sql.append(",t2.profile_picture");
        }
        return sql;
    }

    private StringBuffer generateNormalQuery(List<String> groupList, List<String> metricsList, StringBuffer whereStr) {
        StringBuffer sql = new StringBuffer("SELECT ");
        StringBuffer queryMetricBuffer = new StringBuffer();
        metricsList.stream().forEach(a -> {
            switch (a) {
                case "gmv":
                    queryMetricBuffer.append(" sum(gmv)as gmv");
                    break;
                case "orders":
                    queryMetricBuffer.append(" sum(orders)as orders");
                    break;
                case "video_views":
                    queryMetricBuffer.append(" sum(video_views)as video_views");
                    break;
                case "creator_commission":
                    queryMetricBuffer.append(" sum(creator_commission)as creator_commission");
                    break;
                case "partner_commission":
                    queryMetricBuffer.append(" sum(partner_commission)as partner_commission");
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
                .append(" FROM gmv_detail")
                .append(" WHERE ")
                .append(whereStr)
                .append(" GROUP BY ")
                .append(generateGroup(groupList));
        if (sql != null && groupList.contains("day")) {
            sql.append(" ORDER BY day,gmv desc");
        } else {
            sql.append(" ORDER BY gmv desc");
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
