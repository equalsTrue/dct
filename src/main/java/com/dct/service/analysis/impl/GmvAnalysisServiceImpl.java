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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    @Override
    public void handleTkReport(MultipartFile gmvFile, MultipartFile vidFile, MultipartFile pidFile, MultipartFile creatorFile, String account, String time, String country) {
        handleGmvFile(gmvFile,account,time,country);
        handleVidFile(vidFile,account,time,country);
        handlePidFile(pidFile,account,time,country);
        handleCreatorFile(creatorFile,account,time,country);
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
                    String[] item = line.split(",");
                    String creator = item[vidDataIndex.get(MainConstant.CREATOR)];
                    String product_id = item[vidDataIndex.get(MainConstant.PID)];
                    String vid = item[vidDataIndex.get(MainConstant.VID)];
                    String videoName = item[vidDataIndex.get(MainConstant.VIDEO_NAME)];
                    double gmv = Double.valueOf(generateFormatValue(item[vidDataIndex.get(MainConstant.GMV)]));
                    Integer video_views = Integer.valueOf(generateFormatValue(item[vidDataIndex.get(MainConstant.VIDEO_VIEWS)]));
                    double commission = Double.valueOf(generateFormatValue(item[vidDataIndex.get(MainConstant.COMMISSION)]));
                    model.setDate(time);
                    model.setAccount(account);
                    model.setCreator(creator);
                    model.setProduct_id(product_id);
                    model.setGmv(gmv);
                    model.setVid(vid);
                    model.setVideo_name(videoName);
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
        } catch (Exception e) {
            log.error( "  GENERATE VIDEO DATA ERROR:{}", e.getMessage());
        }
    }

    private void generateVidIndex(BufferedReader reader) {
        try {
            String line = null;
            int indexLine = 0;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(",");
                if (indexLine == 1) {
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
                            case 6:
                                vidDataIndex.put(MainConstant.VIDEO_NAME, i);
                                break;
                            case 8:
                                vidDataIndex.put(MainConstant.PID, i);
                                break;
                            case 12:
                                vidDataIndex.put(MainConstant.GMV, i);
                                break;
                            case 13:
                                vidDataIndex.put(MainConstant.COMMISSION, i);
                                break;
                            case 16:
                                vidDataIndex.put(MainConstant.VIDEO_VIEWS, i);
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
                    String[] item = line.split(",");
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
                    double commission = Double.valueOf(generateFormatValue(item[gmvDataIndex.get(MainConstant.COMMISSION)]));
                    model.setCampaign_id(campaign_id);
                    model.setCampaign_name(campaign_name);
                    model.setCountry(country);
                    model.setDate(time);
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
                    model.setCommission(commission);
                    gmvDetailModels.add(model);
                    index++;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error( "  GENERATE GMV DATA ERROR:{}", e.getMessage());
        }
    }


    /**
     * format num.
     *
     * @param value
     * @return
     */
    private String generateFormatValue(String value) {
        value = StringUtils.isBlank(value) || value.equalsIgnoreCase(MainConstant.MID_LINE) ? "0" : value;
        return value;
    }

    private void generateGmvIndex(BufferedReader reader) {
        try {
            String line = null;
            int indexLine = 0;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(",");
                if (indexLine == 1) {
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
                                gmvDataIndex.put(MainConstant.COMMISSION, i);
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
