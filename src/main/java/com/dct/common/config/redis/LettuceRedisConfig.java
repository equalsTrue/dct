package com.dct.common.config.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * @author Vic on 2020/7/17
 */
@Configuration
public class LettuceRedisConfig {


    /**
     * 主节点(写权限)
     */
    @Value("${spring.redis.host}")
    private String host1;

    @Value("${spring.redis.port}")
    private int port1;

    @Value("${spring.redis.timeout}")
    private long timeout1;

    @Value("${spring.redis.lettuce.shutdown-timeout}")
    private long shutDownTimeout1;

    @Value("${spring.redis.database}")
    private int database1;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int poolMaxIdle1;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int poolMaxActive1;

    @Value("${spring.redis.lettuce.pool.max-wait}")
    private long poolMaxWait1;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int poolMinIdle1;

    /**
     * 配置zoo master lettuce连接池
     *
     * @return
     */
    @Bean
    public GenericObjectPoolConfig redisPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(poolMaxIdle1);
        genericObjectPoolConfig.setMinIdle(poolMinIdle1);
        genericObjectPoolConfig.setMaxTotal(poolMaxActive1);
        genericObjectPoolConfig.setMaxWaitMillis(poolMaxWait1);
        return genericObjectPoolConfig;
    }

    /**
     * 配置zoo master数据源的
     *
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisMasterConfig() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host1);
        redisStandaloneConfiguration.setPort(port1);
        redisStandaloneConfiguration.setDatabase(database1);
        return redisStandaloneConfiguration;
    }




    /**
     * 配置第一个数据源的连接工厂
     * 这里注意：需要添加@Primary 指定bean的名称，目的是为了创建两个不同名称的LettuceConnectionFactory
     *
     * @param config
     * @param redisMasterConfig
     * @return
     */
    @Bean("masterFactory")
    @Primary
    public LettuceConnectionFactory factory(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisMasterConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().shutdownTimeout(Duration.ofMillis(shutDownTimeout1)).commandTimeout(Duration.ofMillis(timeout1)).poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisMasterConfig, clientConfiguration);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }



    /**
     * 配置第一个数据源的RedisTemplate
     * 注意：这里指定使用名称=factory 的 RedisConnectionFactory
     * 并且标识第一个数据源是默认数据源 @Primary
     *
     * @param factory
     * @return
     */
    @Bean
    @Primary
    public StringRedisTemplate masterRedisTemplate(@Qualifier("masterFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }


}