package dev.flanker.banking.infra.ioc;

public interface BeanDefinitionScanner {
    BeanDefinitionContainerBuilder scan(BeanDefinitionContainerBuilder builder);
}
