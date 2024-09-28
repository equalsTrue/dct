package com.dct;

import com.github.hiwepy.ip2region.spring.boot.EnableIP2region;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * LadderServerApplication.
 *
 * @version 1.0
 * @Author Vic.Zhao
 * @Date 2022/4/14 7:58 PM
 */
@EnableCaching
@EnableAsync
@EnableIP2region
@SpringBootApplication
public class DctMangerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DctMangerApplication.class, args);
    }


    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }

}
