package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.BeanDefinitionContainer;
import dev.flanker.banking.infra.ioc.BeanDefinitionContainerBuilder;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapBeanDefinitionContainer implements BeanDefinitionContainer {
    private final Map<String, BeanDefinition> beanDefinitions;

    private final Map<Class<?>, Map<String, BeanDefinition>> beanDefinitionsIndex;

    private MapBeanDefinitionContainer(Map<String, BeanDefinition> beanDefinitions,
                                       Map<Class<?>, Map<String, BeanDefinition>> beanDefinitionsIndex) {
        this.beanDefinitions = beanDefinitions;
        this.beanDefinitionsIndex = beanDefinitionsIndex;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanId) {
        return beanDefinitions.get(beanId);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> beanClass) {
        var beans = beanDefinitionsIndex.get(beanClass);
        if (beans != null && beans.size() == 1) {
            return beans.values()
                    .stream()
                    .findAny()
                    .get();
        } else {
            throw new RuntimeException("Found multiple implementations of class [" + beanClass.getSimpleName() + "]");
        }
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinitions(Class<?> beanClass) {
        return new HashMap<>(beanDefinitionsIndex.get(beanClass));
    }

    @Override
    public Map<String, BeanDefinition> getAllBeanDefinitions() {
        return new HashMap<>(beanDefinitions);
    }

    public static BeanDefinitionContainerBuilder builder() {
        return new HashMapBeanDefinitionContainerBuilder();
    }

    private static class HashMapBeanDefinitionContainerBuilder implements BeanDefinitionContainerBuilder {
        private final ConcurrentMap<String, BeanDefinition> beanDefinitions;

        private HashMapBeanDefinitionContainerBuilder() {
            this.beanDefinitions = new ConcurrentHashMap<>();
        }

        private HashMapBeanDefinitionContainerBuilder(ConcurrentMap<String, BeanDefinition> beanDefinitions) {
            this.beanDefinitions = new ConcurrentHashMap<>(beanDefinitions);
        }

        @Override
        public BeanDefinitionContainerBuilder add(BeanDefinition beanDefinition) {
            if (beanDefinitions.containsKey(beanDefinition.getBeanId())) {
                throw new RuntimeException("Already contains with key [" + beanDefinition.getBeanId() + "]");
            }
            beanDefinitions.put(beanDefinition.getBeanId(), beanDefinition);
            return this;
        }

        @Override
        public BeanDefinitionContainer build() {
            return new MapBeanDefinitionContainer(new HashMap<>(beanDefinitions), buildIndex());
        }

        private Map<Class<?>, Map<String, BeanDefinition>> buildIndex() {
            var beansIndex = new HashMap<Class<?>, Map<String, BeanDefinition>>();
            for (var beanPair : beanDefinitions.entrySet()) {
                var beanDefinition = beanPair.getValue();
                var beanId = beanPair.getKey();

                beansIndex.putIfAbsent(beanDefinition.getBeanClass(), new HashMap<>());
                beansIndex.get(beanDefinition.getBeanClass()).put(beanId, beanDefinition);
                for (var beanInterface : beanDefinition.getBeanInterfaces()) {
                    beansIndex.putIfAbsent(beanInterface, new HashMap<>());
                    beansIndex.get(beanInterface).put(beanId, beanDefinition);
                }

            }
            return beansIndex;
        }
    }
}
