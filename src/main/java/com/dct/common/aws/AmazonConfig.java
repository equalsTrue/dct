package com.dct.common.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/13.
 */
@Configuration
@Slf4j
public class AmazonConfig {

    @Bean(name = "credentials")
    public BasicAWSCredentials s3BasicAWSCredentials(){
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(System.getProperty("aws.access_key"), System.getProperty("aws.secret_access_key"));
        return basicAWSCredentials;
    }

    @Bean(name = "s3Client")
    public AmazonS3 s3ConfigBean(){
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion("eu-central-1")
                .withCredentials(new AWSStaticCredentialsProvider(this.s3BasicAWSCredentials()))
                .build();
        return amazonS3;
    }
}
