package com.volmyr.shopizer.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Configure Liquibase but run it in {@link ApplicationListenerConfig}
 * after complete initialization of database.
 */
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfiguration extends SpringLiquibase {

    private final LiquibaseProperties properties;
    private final DataSource dataSource;

    public LiquibaseConfiguration(LiquibaseProperties properties, DataSource dataSource) {
        this.properties = properties;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void setUp() {
        this.setChangeLog(this.properties.getChangeLog());
        this.setContexts(this.properties.getContexts());
        this.setDataSource(dataSource);
        this.setDefaultSchema(this.properties.getDefaultSchema());
        this.setShouldRun(false);
        this.setChangeLogParameters(this.properties.getParameters());
    }
}
