package com.dct.utils;

import com.alibaba.fastjson.JSONObject;
import com.dct.constant.enums.ResponseInfoEnum;
import com.dct.model.vo.ResponseInfoVO;

public class JSONObjectResponseInfoUtil {

//    public static JSONObject invalidUser() {
//        ResponseInfoVO returnMsg = new ResponseInfoVO();
//        returnMsg.setCode(ResponseInfoEnum.INVALID_USER.getCode());
//        returnMsg.setMsg(ResponseInfoEnum.INVALID_USER.getMsg());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("returnMsg", returnMsg);
//        return jsonObject;
//    }

    /**
     * 返回未知错误的响应结果.
     *
     * @param obj
     * @return
     */
    public static JSONObject error(Object obj) {
        ResponseInfoVO msg = new ResponseInfoVO();
        msg.setCode(ResponseInfoEnum.UNKNOWN_ERROR.getCode());
        msg.setMsg(ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
        msg.setData(obj);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("returnMsg", msg);
        return jsonObject;
    }

    /**
     * 返回未知错误的响应结果.
     *
     * @return
     */
    public static JSONObject error() {
        ResponseInfoVO msg = new ResponseInfoVO();
        msg.setCode(ResponseInfoEnum.UNKNOWN_ERROR.getCode());
        msg.setMsg(ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("returnMsg", msg);
        return jsonObject;
    }
}
