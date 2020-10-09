package dev.flanker.banking.infra.ioc.domain;

import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.BeanProxyPostProcessor;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

public record ConfigurationMetadata(
        Set<String> propertySources,
        Set<String> packages,
        Set<Method> beanPostProcessors,
        Set<Method> beanProxyPostProcessor
) {}
