package dev.flanker.banking.infra.ioc.domain;

import java.lang.reflect.Method;
import java.util.Set;

public record ConfigurationMetadata(
        Set<String> propertySources,
        Set<String> packages,
        Set<Method> beanPostProcessors,
        Set<Method> beanProxyPostProcessor
) {}
