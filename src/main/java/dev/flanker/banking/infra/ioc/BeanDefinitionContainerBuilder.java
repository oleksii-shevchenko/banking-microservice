package dev.flanker.banking.infra.ioc;

public interface BeanContainerBuilder {
    BeanContainerBuilder add(String beanId, Object bean);

    BeanDefinitionContainer build();
}
