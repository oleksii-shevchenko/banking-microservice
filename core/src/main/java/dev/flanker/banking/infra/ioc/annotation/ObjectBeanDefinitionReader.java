package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import java.util.List;

public interface ObjectBeanDefinitionReader {
    List<BeanDefinition> read(Object source);
}
