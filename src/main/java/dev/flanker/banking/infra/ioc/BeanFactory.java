package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;

public interface BeanFactory {
    Object createBean(BeanDefinition beanDefinition);
}
