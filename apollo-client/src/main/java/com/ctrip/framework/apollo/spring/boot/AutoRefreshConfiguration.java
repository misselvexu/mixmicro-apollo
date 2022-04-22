package com.ctrip.framework.apollo.spring.boot;

import com.ctrip.framework.apollo.spring.refresh.SmartConfigurationPropertiesRebinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link AutoRefreshConfiguration} Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2022/4/14
 */
@Configuration
public class AutoRefreshConfiguration {

  @Bean
  @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
  public SmartConfigurationPropertiesRebinder smartConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
    return new SmartConfigurationPropertiesRebinder(beans);
  }

}
