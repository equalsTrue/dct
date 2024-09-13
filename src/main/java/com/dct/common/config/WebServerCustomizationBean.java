package com.dct.common.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * 主要解决请求url携带了特殊符号的参数，导致出现状态码400的错误
 *
 * @author MoCha
 * @date 2019/9/30
 */
@Component
public class WebServerCustomizationBean implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> connector.setAttribute("relaxedQueryChars", "[]{}!*#?+"));
    }
}
