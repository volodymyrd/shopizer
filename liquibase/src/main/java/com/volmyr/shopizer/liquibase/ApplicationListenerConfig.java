package com.volmyr.shopizer.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Uses for run logic after container fully initialized.
 */
@Component
public class ApplicationListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerConfig.class);

  private final ApplicationContext appContext;

  public ApplicationListenerConfig(ApplicationContext appContext) {
    this.appContext = appContext;
  }

  private boolean init = false;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOG.info("ApplicationListenerConfig start...");
    if (!init) {
      try {
        SpringLiquibase liquibase = appContext.getBean(SpringLiquibase.class);
        liquibase.setShouldRun(true);
        liquibase.afterPropertiesSet();
        init = true;
      } catch (LiquibaseException e) {
        LOG.error("Error running Liquibase", e);
      }
    }
  }
}
