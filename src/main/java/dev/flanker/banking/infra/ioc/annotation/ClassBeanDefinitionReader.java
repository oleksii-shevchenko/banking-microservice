package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;

public interface AnnotationBeanDefinitionReader {
    BeanDefinition read(Class<?> source);
}
