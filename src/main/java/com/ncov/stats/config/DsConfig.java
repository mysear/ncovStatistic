package com.ncov.stats.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * 功能描述: 数据库配置
 *
 * @param
 * @author : contact@vector.link
 * @return :
 * @date : 2019/10/29 19:09
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.ncov.stats.repository"},
        entityManagerFactoryRef = "firstEntityManagerFactory", transactionManagerRef = "firstTransactionManager")
public class DsConfig {
    @Bean
    @ConfigurationProperties("first.datasource")
    public DataSourceProperties firstDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("first.datasource")
    public DataSource firstDataSource() {
        return firstDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * 实体管理对象
     *
     * @param builder 由spring注入这个对象，首先根据type注入（多个就取声明@Primary的对象），否则根据name注入
     * @return
     */
    @Bean(name = "firstEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(firstDataSource())
                .packages("com.ncov.stats.entity")
                .persistenceUnit("firstDs")
                .build();
    }

    /**
     * 事物管理对象
     *
     * @param firstEntityManagerFactory 实体管理工厂对象（按照名称注入）
     * @return 平台事物管理器
     */
    @Bean(name = "firstTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("firstEntityManagerFactory") LocalContainerEntityManagerFactoryBean firstEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(firstEntityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean(name = "jdbcTemplate2")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(firstDataSource());
    }

    @Bean(name = "transactionTemplate2")
    public TransactionTemplate transactionTemplate(@Qualifier("firstTransactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
