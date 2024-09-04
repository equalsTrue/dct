package com.dct.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Http工具类.
 *
 * @author magic
 * @date 2021/1/8
 */
@Slf4j
public class HttpUtil {

    private static final String UNKNOWN = "unknown";

    private static final String LOCAL_IP = "127.0.0.1";

    private static final String COMMA = ",";

    private static final int IP_LENGTH = 15;

    public static final String BLANK = " ";

    private static final int SUCCESS_CONNECT = 200;

    public static String getIpAddr(HttpServletRequest request) {

        if (request == null) {
            return UNKNOWN;
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.length() > IP_LENGTH && ip.indexOf(COMMA) > 0) {
            ip = ip.substring(0, ip.indexOf(COMMA));
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }

    private static String getCorrectUrl(String url) {
        url = StringUtils.trim(url);
        try {
            url = url.replace(BLANK, URLEncoder.encode(BLANK, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
        return url;
    }

    public static CloseableHttpClient getHttpClient() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(20 * 1000)
                .setConnectionRequestTimeout(20 * 1000)
                .setSocketTimeout(10 * 60 * 1000)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
    }

    /**
     * get方法.
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        String correctUrl = getCorrectUrl(url);
        HttpGet httpRequest = new HttpGet(correctUrl);
        String strResult = "";
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == SUCCESS_CONNECT) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
            } else {
                strResult = "error";
                httpRequest.abort();
            }
        } catch (Exception e) {
            httpRequest.abort();
            strResult = "error";
            log.error("httpUtil doGet request Error, url:{}, error:{}", correctUrl, e.getMessage(), e);
        } finally {
            if (null != httpRequest) {
                httpRequest.releaseConnection();
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strResult;
    }

    /**
     * POST method.
     *
     * @param url
     * @param jsonBody
     * @return
     */
    public static String doPost(String url, String jsonBody) {
        HttpPost httpPost = new HttpPost(url);
        String strResult = "";
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(jsonBody, "UTF-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == SUCCESS_CONNECT) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
            } else {
                strResult = "error";
                httpPost.abort();
            }
        } catch (Exception e) {
            httpPost.abort();
            strResult = "error";
            log.error("HttpUtil doPost request Error, url:{}, error:{}", url, e.getMessage(), e);
        } finally {
            httpPost.releaseConnection();
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
