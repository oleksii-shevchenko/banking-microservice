package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import dev.flanker.banking.infra.ioc.impl.DefaultBeanDefinitionContainer;

public interface BeanDefinitionContainerBuilder {
    BeanDefinitionContainerBuilder add(BeanDefinition beanDefinition);

    BeanDefinitionContainer build();

    static BeanDefinitionContainerBuilder builder() {
        return DefaultBeanDefinitionContainer.builder();
    }
}
