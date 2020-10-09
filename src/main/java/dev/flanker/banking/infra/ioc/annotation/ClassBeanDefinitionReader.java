package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import java.util.Collection;

public interface ClassBeanDefinitionReader {
    BeanDefinition read(Class<?> source);
}
