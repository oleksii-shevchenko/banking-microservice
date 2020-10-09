package dev.flanker.banking.infra.ioc;

import java.util.Map;

public interface BeanContainer {
    Object getBean(String beanId);

    Object getBean(Class<?> beanClass);

    Map<String, Object> getBeans(Class<?> beanClass);

    Map<String, Object> getAllBeans();
}
