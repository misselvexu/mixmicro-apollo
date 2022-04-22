package com.ctrip.framework.apollo.spring.refresh;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationPropertiesBean;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * {@link SmartConfigurationPropertiesRebinder} Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2022/4/14
 */
public class SmartConfigurationPropertiesRebinder extends ConfigurationPropertiesRebinder {


  private Map<String, ConfigurationPropertiesBean> beanMap;

  private ApplicationContext applicationContext;


  public SmartConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
    super(beans);
    fillBeanMap(beans);
  }

  @SuppressWarnings("unchecked")
  private void fillBeanMap(ConfigurationPropertiesBeans beans) {
    this.beanMap = new HashMap<>();
    Field field = ReflectionUtils.findField(beans.getClass(), "beans");
    if (field != null) {
      field.setAccessible(true);
      this.beanMap.putAll((Map<String, ConfigurationPropertiesBean>) Optional
          .ofNullable(ReflectionUtils.getField(field, beans))
          .orElse(Collections.emptyMap()));
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    super.setApplicationContext(applicationContext);
    this.applicationContext = applicationContext;
  }

  @Override
  public void onApplicationEvent(EnvironmentChangeEvent event) {
    if (this.applicationContext.equals(event.getSource())
        // Backwards compatible
        || event.getKeys().equals(event.getSource())) {
      rebind();
    }
  }

  private void rebindSpecificBean(EnvironmentChangeEvent event) {
    Set<String> refreshedSet = new HashSet<>();
    beanMap.forEach((name, bean) -> event.getKeys().forEach(changeKey -> {
      String prefix = AnnotationUtils.getValue(bean.getAnnotation()).toString();
      // prevent multiple refresh one ConfigurationPropertiesBean.
      if (changeKey.startsWith(prefix) && refreshedSet.add(name)) {
        rebind(name);
      }
    }));
  }
}
