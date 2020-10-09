package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;

public interface BeanPostProcessor {
    void configure(Object bean, BeanDefinition beanDefinition, ApplicationContext applicationContext);
}
