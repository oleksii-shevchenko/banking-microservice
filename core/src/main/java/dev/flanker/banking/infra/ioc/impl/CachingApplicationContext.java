package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.BeanDefinitionContainer;
import dev.flanker.banking.infra.ioc.ContextAwareBeanFactory;
import dev.flanker.banking.infra.ioc.Environment;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.flanker.banking.infra.ioc.util.BeanUtils.invokePreDestroy;

public class CachingApplicationContext implements ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachingApplicationContext.class);

    private final BeanDefinitionContainer beanDefinitions;

    private final ContextAwareBeanFactory beanFactory;

    private final ConcurrentMap<String, Object> beans;

    private final Environment environment;

    public CachingApplicationContext(BeanDefinitionContainer beanDefinitions,
                                     ContextAwareBeanFactory beanFactory,
                                     Environment environment) {
        this(beanDefinitions, beanFactory, environment, new ConcurrentHashMap<>());
    }

    public CachingApplicationContext(BeanDefinitionContainer beanDefinitions,
                                     ContextAwareBeanFactory beanFactory,
                                     Environment environment,
                                     ConcurrentMap<String, Object> beans) {
        this.beanDefinitions = beanDefinitions;
        this.beanFactory = beanFactory;
        this.environment = environment;
        this.beans = beans;
    }

    @Override
    public void start() {

        beanFactory.set(this);

        // Create singletons
        beanDefinitions.getAllBeanDefinitions()
                .values()
                .stream()
                .filter(BeanDefinition::eagerCreated)
                .map(BeanDefinition::id)
                .forEach(this::getBean);

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public Object getBean(String beanId) {
        if (beanDefinitions.containsBeanDefinition(beanId)) {
            return beans.computeIfAbsent(beanId, id -> beanFactory.createBean(beanDefinitions.getBeanDefinition(id)));
        } else {
            return null;
        }
    }

    @Override
    public <T> T getBean(String beanId, Class<T> beanClass) {
        var bean = getBean(beanId);
        if (bean != null && beanClass.isAssignableFrom(bean.getClass())) {
            return beanClass.cast(getBean(beanId));
        } else {
            return null;
        }
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        if (!beanDefinitions.containsBeanDefinition(beanClass)) {
            return null;
        }
        var beanDefinition = beanDefinitions.getBeanDefinition(beanClass);
        return getBean(beanDefinition.id(), beanClass);
    }

    @Override
    public <T> Collection<? extends T> getBeans(Class<T> beanClass) {
        return beanDefinitions.getBeanDefinitions(beanClass)
                .values()
                .stream()
                .map(beanDefinition -> getBean(beanDefinition.id(), beanClass))
                .collect(Collectors.toSet());
    }

    @Override
    public void close() {
        beanDefinitions.getAllBeanDefinitions()
                .values()
                .forEach(bd -> bd.preDestroyMethods().forEach(method -> invokePreDestroy(getBean(bd.id()), method)));
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }
}
