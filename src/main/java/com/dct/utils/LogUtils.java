package com.dct.utils;


import java.util.UUID;

public class LogUtils {
    public static String getLogId(String[] paramsArray) {
        String logId;
        //最后一位添加uuid进行区分
        if (paramsArray != null && paramsArray.length > 0 && paramsArray[paramsArray.length - 1].length() >= 30) {
            logId = paramsArray[paramsArray.length - 1];
        } else {
            logId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return logId;
    }

    public static boolean isLastElement30Digits(String[] paramsArray) {
        if (paramsArray != null && paramsArray.length > 0) {
            String lastElement = paramsArray[paramsArray.length - 1];
            return lastElement != null && lastElement.length() >= 30;
        }
        return false;
    }

    //区分符 新老 进行区分 提到公共工具类中
    //判断identifier位数，进行提取identifier
    public static String getIdentifier(String[] paramsArray) {
        String identifier = null;
        if (paramsArray != null && paramsArray.length > 0) {
            int length = paramsArray[0].length();
            //区分符号 位数为 3+4+1 还是 5+4+1
            if (length == 8 ){
                identifier = paramsArray[0].substring(3,8);
            }else if (length == 10){
                identifier = paramsArray[0].substring(5,10);
            }
        }
        return identifier;
    }


    public static String getVersion(String[] paramsArray) {
        String identifier = null;
        if (paramsArray != null && paramsArray.length > 0) {
            int length = paramsArray[0].length();
            //区分符号 位数为 3+4+1 还是 5+4+1
            if (length == 8 ){
                identifier = paramsArray[0].substring(0,3).replaceAll("", ".");
                identifier = identifier.substring(1, identifier.length() - 1);
            }else if (length == 10){
                identifier = paramsArray[0].substring(0,5);
                //算法计算
                String versionOne = identifier.substring(0,1);
                int versionTwo = Integer.parseInt(identifier.substring(1,3));
                int versionThree = Integer.parseInt(identifier.substring(3,5));
                identifier = versionOne + "." + versionTwo + "." +versionThree;
            }
        }
        //返回version
        return identifier;
    }

}
