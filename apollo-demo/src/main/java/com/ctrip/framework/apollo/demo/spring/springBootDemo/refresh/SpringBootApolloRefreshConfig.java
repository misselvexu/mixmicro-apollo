/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.demo.spring.springBootDemo.refresh;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.demo.spring.springBootDemo.config.SampleRedisConfig;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@ConditionalOnProperty("redis.cache.enabled")
@Component
public class SpringBootApolloRefreshConfig implements ApplicationEventPublisherAware , EnvironmentAware {
  private static final Logger logger = LoggerFactory.getLogger(SpringBootApolloRefreshConfig.class);

  private final SampleRedisConfig sampleRedisConfig;
  private final RefreshScope refreshScope;


  private Environment environment;

  private ApplicationEventPublisher publisher;

  public SpringBootApolloRefreshConfig(
      final SampleRedisConfig sampleRedisConfig,
      final RefreshScope refreshScope) {
    this.sampleRedisConfig = sampleRedisConfig;
    this.refreshScope = refreshScope;
  }

  @ApolloConfigChangeListener(value = {ConfigConsts.NAMESPACE_APPLICATION}, interestedKeyPrefixes = {"redis.cache."})
  public void onChange(ConfigChangeEvent changeEvent) {
    logger.info("before refresh {}", sampleRedisConfig.toString());
//    refreshScope.refresh("sampleRedisConfig");
    this.publisher.publishEvent(new EnvironmentChangeEvent(publisher, Collections.emptySet()));
    logger.info("after refresh {}", sampleRedisConfig.toString());
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;

  }
}
