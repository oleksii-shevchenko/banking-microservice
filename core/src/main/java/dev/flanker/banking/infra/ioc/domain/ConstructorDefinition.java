package dev.flanker.banking.infra.ioc.domain;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public record ConstructorDefinition(
        Function<Object[], Object> constructor,
        Class<?>[] parametersTypes,
        Annotation[][] parametersAnnotations,
        int parametersCount
) {}
