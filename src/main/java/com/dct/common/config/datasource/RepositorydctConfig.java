package com.dct.common.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据库 配置.
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "dctEntityManagerFactory",
        transactionManagerRef = "dctTransactionManager",
        basePackages = {"com.dct.repo.*"})
public class RepositorydctConfig {

    /**
     * 扫描spring.jpa.primary开头的配置信息
     *
     * @return jpa配置信息
     */
    @Primary
    @Bean(name = "dctJpaProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.dct.jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    /**
     * 获取主库实体管理工厂对象
     *
     * @param jpaProperties     注入名为primaryJpaProperties的jpa配置信息
     * @param builder           注入EntityManagerFactoryBuilder
     * @return 实体管理工厂对象
     */
    @Primary
    @Bean(name = "dctEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,@Qualifier("dctJpaProperties") JpaProperties jpaProperties,
            HibernateProperties hibernateProperties) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());
        return builder.dataSource(dctDataSource()).properties(properties).persistenceUnit("dctEntityManager")
                .packages("com.dct.model.dct").build();
    }


    /**
     * 获取主库事务管理对象
     *
     * @param factory 注入名为primaryEntityManagerFactory的bean
     * @return 事务管理对象
     */
    @Primary
    @Bean(name = "dctTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dctEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

    /**
     * 获取实体管理对象
     *
     * @param factory 注入名为primaryEntityManagerFactory的bean
     * @return 实体管理对象
     */
    @Primary
    @Bean(name = "dctEntityManager")
    public EntityManager entityManager(@Qualifier("dctEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }


    @Primary
    @Bean(name = "dctDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.dct")
    public DataSourceProperties dctDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dctDataSource")
    @Qualifier("dctDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.dct")
    public DataSource dctDataSource() {
        return DruidDataSourceBuilder.create().build();
    }
}
