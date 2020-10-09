package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;

public interface ClassBeanDefinitionReader {
    BeanDefinition read(Class<?> source);
}
