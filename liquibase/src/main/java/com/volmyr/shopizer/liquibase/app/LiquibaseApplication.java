package com.volmyr.shopizer.liquibase.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Liquibase Application Entry Point.
 */
@SpringBootApplication(exclude = {
        LiquibaseAutoConfiguration.class, LdapAutoConfiguration.class, QuartzAutoConfiguration.class})
@ComponentScan(basePackages = {"com.salesmanager", "com.volmyr"})
@EntityScan(basePackages = {"com.salesmanager.core.model"})
public class LiquibaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiquibaseApplication.class, args);
    }
}
