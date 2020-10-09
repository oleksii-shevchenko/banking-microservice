package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import java.util.Map;

public interface BeanDefinitionContainer {
    BeanDefinition getBeanDefinition(String beanId);

    BeanDefinition getBeanDefinition(Class<?> beanClass);

    Map<String, BeanDefinition> getBeanDefinitions(Class<?> beanClass);

    Map<String, BeanDefinition> getAllBeanDefinitions();

    boolean containsBeanDefinition(String beanId);

    boolean containsBeanDefinition(Class<?> beanClass);
}
