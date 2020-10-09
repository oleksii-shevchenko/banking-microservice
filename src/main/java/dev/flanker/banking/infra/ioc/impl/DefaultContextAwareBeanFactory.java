package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.BeanFactory;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import lombok.SneakyThrows;

public class ContextAwareBeanFactory implements BeanFactory {
    private final ApplicationContext applicationContext;

    private ContextAwareBeanFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @SneakyThrows
    public Object createBean(BeanDefinition beanDefinition) {
        var constructor = beanDefinition.getBeanConstructor();

        var parameters = constructor.getParameterTypes();
        var parameterAnnotations = constructor.getParameterAnnotations();

        var injected = new Object[constructor.getParameterCount()];

        for (int i = 0; i < constructor.getParameterCount(); i++) {
            var beanId = getBeanIdOrNull(parameterAnnotations[i]);
            if (beanId != null) {
                injected[i] = applicationContext.getBean(beanId);
            } else {
                injected[i] = applicationContext.getBean(parameters[i]);
            }
        }

        constructor.setAccessible(true);
        return constructor.newInstance(injected);
    }

    private String getBeanIdOrNull(Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof Named) {
                return ((Named) parameterAnnotation).value();
            }
        }
        return null;
    }
}
