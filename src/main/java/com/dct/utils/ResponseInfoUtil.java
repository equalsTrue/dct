package com.dct.utils;


import com.dct.constant.enums.ResponseInfoEnum;
import com.dct.model.vo.ResponseInfoVO;

/**
 * Http请求返回值工具类.
 *
 * @author magic
 * @data 2022/4/19
 */
public class ResponseInfoUtil {

    /**
     * 请求成功返回success.
     *
     * @param obj
     * @return ResponseInfo
     * @throws NullPointerException when obj is null
     */
    public static ResponseInfoVO success(Object obj) {
        ResponseInfoVO msg = new ResponseInfoVO();
        msg.setCode(ResponseInfoEnum.SUCCESS.getCode());
        msg.setMsg(ResponseInfoEnum.SUCCESS.getMsg());
        msg.setData(obj);
        return msg;
    }
    /**
     * 请求成功返回Data.
     *
     * @param obj
     * @return
     */
    public static ResponseInfoVO setData(Object obj) {
        ResponseInfoVO msg = new ResponseInfoVO();
        msg.setData(obj);
        return msg;
    }

    /**
     * 返回data为空的成功结果.
     *
     * @return ResponseInfo
     */
    public static ResponseInfoVO success() {
        return success(null);
    }



    /**
     * 请求成功返回ok.
     *
     * @param obj
     * @return
     */
    public static ResponseInfoVO ok(Object obj) {
        ResponseInfoVO msg = new ResponseInfoVO();
        msg.setCode(ResponseInfoEnum.OK.getCode());
        msg.setMsg(ResponseInfoEnum.OK.getMsg());
        msg.setData(obj);
        return msg;
    }

    /**
     * 返货msg为ok，data为null的结果.
     *
     * @return
     */
    public static ResponseInfoVO ok() {
        return ok(null);
    }

    /**
     * 返回错误响应结果.
     *
     * @param code
     * @param msg
     * @return
     */
    public static ResponseInfoVO error(Integer code, String msg) {
        ResponseInfoVO returnMsg = new ResponseInfoVO();
        returnMsg.setCode(code);
        returnMsg.setMsg(msg);
        return returnMsg;
    }

    /**
     * 返回未知错误的响应结果.
     *
     * @param obj
     * @return
     */
    public static ResponseInfoVO error(Object obj) {
        ResponseInfoVO msg = new ResponseInfoVO();
        msg.setCode(ResponseInfoEnum.UNKNOWN_ERROR.getCode());
        msg.setMsg(ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
        msg.setData(obj);
        return msg;
    }

    /**
     * 返回data为空的未知错误响应结果.
     *
     * @return
     */
    public static ResponseInfoVO error() {
        ResponseInfoVO returnMsg = new ResponseInfoVO();
        returnMsg.setCode(ResponseInfoEnum.UNKNOWN_ERROR.getCode());
        returnMsg.setMsg(ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
        return returnMsg;
    }

    /**
     * 返回非法用户响应结果.
     *
     * @return
     */
//    public static ResponseInfoVO invalidUser() {
//        ResponseInfoVO returnMsg = new ResponseInfoVO();
//        returnMsg.setCode(ResponseInfoEnum.INVALID_USER.getCode());
//        returnMsg.setMsg(ResponseInfoEnum.INVALID_USER.getMsg());
//        return returnMsg;
//    }

    /**
     * 返回非法用户响应结果.
     *
     * @return
     */
    public static ResponseInfoVO invalidApp() {
        ResponseInfoVO returnMsg = new ResponseInfoVO();
        returnMsg.setCode(ResponseInfoEnum.INVALID_APP.getCode());
        returnMsg.setMsg(ResponseInfoEnum.INVALID_APP.getMsg());
        return returnMsg;
    }

}
