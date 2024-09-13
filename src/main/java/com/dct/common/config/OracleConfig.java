package com.dct.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author Vic.Zhao
 * @Date 2023/4/24 16:54
 */
@ConfigurationProperties(prefix = "oracle")
@Component
@Data
public class OracleConfig {

    private String sshPubKeyFilePath;

    private String comptentId;

    private String shapeName;

    private String systemName;

    private String systemVersion;

    private String systemDisplayName;

    private String subnetName;

    private String  imageName;

    private String  serverIp;

    private String  maxRetries;

    private String  retryDelayMs;
}
