package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.BeanProxyPostProcessor;
import dev.flanker.banking.infra.ioc.ContextAwareBeanFactory;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.flanker.banking.infra.ioc.util.BeanUtils.invokePostConstruct;

public class DefaultContextAwareBeanFactory implements ContextAwareBeanFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContextAwareBeanFactory.class);

    private final Collection<BeanPostProcessor> beanPostProcessors;

    private final Collection<BeanProxyPostProcessor> beanProxyPostProcessors;

    private volatile ApplicationContext applicationContext;

    public DefaultContextAwareBeanFactory() {
        this.beanProxyPostProcessors = new ArrayList<>();
        this.beanPostProcessors = new ArrayList<>();
    }

    public DefaultContextAwareBeanFactory(Collection<BeanPostProcessor> beanPostProcessors,
                                          Collection<BeanProxyPostProcessor> beanProxyPostProcessors) {
        this.beanPostProcessors = new ArrayList<>(beanPostProcessors);
        this.beanProxyPostProcessors = new ArrayList<>(beanProxyPostProcessors);
    }

    @Override
    public void set(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object createBean(BeanDefinition beanDefinition) {
        var constructorDefinition = beanDefinition.constructorDefinition();

        var constructor = constructorDefinition.constructor();

        var parameters = constructorDefinition.parametersTypes();
        var parameterAnnotations = constructorDefinition.parametersAnnotations();

        var injected = new Object[constructorDefinition.parametersCount()];

        for (int i = 0; i < constructorDefinition.parametersCount(); i++) {
            var beanId = getBeanIdOrNull(parameterAnnotations[i]);
            if (beanId != null) {
                injected[i] = applicationContext.getBean(beanId);
            } else {
                injected[i] = applicationContext.getBean(parameters[i]);
            }
        }

        var bean = constructor.apply(injected);

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            beanPostProcessor.configure(bean, beanDefinition, applicationContext);
        }
        
        if (!beanDefinition.postConstructMethods().isEmpty()) {
            postConstructBean(bean, beanDefinition);
        }

        for (BeanProxyPostProcessor beanProxyPostProcessor : beanProxyPostProcessors) {
            bean = beanProxyPostProcessor.configure(bean, beanDefinition, applicationContext);
        }

        LOGGER.info("Created bean [id={}]", beanDefinition.id());

        return bean;
    }

    private String getBeanIdOrNull(Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof Named) {
                return ((Named) parameterAnnotation).value();
            }
        }
        return null;
    }

    private void postConstructBean(Object bean, BeanDefinition beanDefinition) {
        beanDefinition.postConstructMethods().forEach(method -> invokePostConstruct(bean, method));
    }
}
