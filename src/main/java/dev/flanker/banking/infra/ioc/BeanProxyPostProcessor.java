package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;

public interface BeanProxyPostProcessor {
    Object configure(Object bean, BeanDefinition beanDefinition, ApplicationContext applicationContext);
}
