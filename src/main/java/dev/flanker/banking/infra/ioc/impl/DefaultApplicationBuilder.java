package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.ApplicationContextBuilder;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.BeanProxyPostProcessor;
import dev.flanker.banking.infra.ioc.annotation.AnnotatedClassBeanDefinitionReader;
import dev.flanker.banking.infra.ioc.annotation.ConfigurationBeanDefinitionScanner;
import dev.flanker.banking.infra.ioc.annotation.ConfigurationObjectBeanDefinitionReader;
import dev.flanker.banking.infra.ioc.annotation.ImportAnnotationConfigurationClassScanner;
import dev.flanker.banking.infra.ioc.annotation.PackageBeanDefinitionScanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultApplicationContextBuilder implements ApplicationContextBuilder {
    private Set<String> basePackages = new HashSet<>();
    private Set<Class<?>> baseConfigurations = new HashSet<>();
    private Set<String> baseProperties = new HashSet<>();
    private Set<Class<? extends BeanPostProcessor>> beanPostProcessors = new HashSet<>();
    private Set<Class<? extends BeanProxyPostProcessor>> beanProxyPostProcessor = new HashSet<>();
    private String[] args = {};
    private boolean autoConfigurationLoad = false;

    @Override
    public ApplicationContextBuilder addPackageScan(String pkg) {
        basePackages.add(pkg);
        return this;
    }

    @Override
    public ApplicationContextBuilder addConfiguration(Class<?> cfg) {
        baseConfigurations.add(cfg);
        return this;
    }

    @Override
    public ApplicationContextBuilder setAutoConfigurationLoad(boolean flg) {
        autoConfigurationLoad = flg;
        return this;
    }

    @Override
    public ApplicationContextBuilder addPropertySource(String src) {
        baseProperties.add(src);
        return this;
    }

    @Override
    public ApplicationContextBuilder addBeanPostProcessor(Class<? extends BeanPostProcessor> bpp) {
        beanPostProcessors.add(bpp);
        return this;
    }

    @Override
    public ApplicationContextBuilder addBeanProxyPostProcessor(Class<? extends BeanProxyPostProcessor> bpp) {
        beanProxyPostProcessor.add(bpp);
        return this;
    }

    @Override
    public ApplicationContextBuilder setArgs(String[] args) {
        this.args = Arrays.copyOf(args, args.length);
        return this;
    }

    @Override
    public ApplicationContext build() throws Exception {
        var beanDefinitionContainerBuilder = DefaultBeanDefinitionContainer.builder();

        var classBeanDefinitionReader = new AnnotatedClassBeanDefinitionReader();
        for (String basePackage : basePackages) {
            new PackageBeanDefinitionScanner(basePackage, classBeanDefinitionReader).scan(beanDefinitionContainerBuilder);
        }

        var configurations = new HashSet<Class<?>>();
        for (Class<?> baseConfiguration : baseConfigurations) {
            configurations.addAll(new ImportAnnotationConfigurationClassScanner(baseConfiguration).scan());
        }

        var configurationInstances = new ArrayList<>();
        for (Class<?> configuration : configurations) {
            configurationInstances.add(configuration.getConstructor().newInstance());
        }

        var objectBeanDefinitionReader = new ConfigurationObjectBeanDefinitionReader();
        new ConfigurationBeanDefinitionScanner(configurationInstances, objectBeanDefinitionReader).scan(beanDefinitionContainerBuilder);

        var beanDefinitions = beanDefinitionContainerBuilder.build();


        return null;
    }
}
