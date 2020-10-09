package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.ApplicationBuilder;
import dev.flanker.banking.infra.ioc.BeanDefinitionContainer;
import dev.flanker.banking.infra.ioc.BeanDefinitionContainerBuilder;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.BeanProxyPostProcessor;
import dev.flanker.banking.infra.ioc.ConfigurationPostProcessor;
import dev.flanker.banking.infra.ioc.Environment;
import dev.flanker.banking.infra.ioc.annotation.ConfigurationMetadataReader;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import dev.flanker.banking.infra.ioc.domain.ConfigurationMetadata;
import dev.flanker.banking.infra.ioc.environment.FilePropertiesLoader;
import dev.flanker.banking.infra.ioc.environment.SystemPropertiesLoader;
import dev.flanker.banking.infra.ioc.annotation.ConfigurationObjectBeanDefinitionReader;
import dev.flanker.banking.infra.ioc.scanner.ImportAnnotationConfigurationClassScanner;
import dev.flanker.banking.infra.ioc.scanner.PackageBeanDefinitionScanner;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.reflections.Reflections;

public class DefaultApplicationBuilder implements ApplicationBuilder {
    private final Set<String> basePackages = new HashSet<>();
    private final Set<Class<?>> baseConfigurations = new HashSet<>();
    private final Set<String> baseProperties = new HashSet<>(Set.of("application.properties"));

    private final Set<Class<? extends BeanPostProcessor>> beanPostProcessorClasses = new HashSet<>();
    private final Set<Class<? extends BeanProxyPostProcessor>> beanProxyPostProcessorClasses = new HashSet<>();

    private String[] args;

    @Override
    public ApplicationBuilder addPackageScan(String pkg) {
        basePackages.add(pkg);
        return this;
    }

    @Override
    public ApplicationBuilder addConfiguration(Class<?> cfg) {
        baseConfigurations.add(cfg);
        return this;
    }

    @Override
    public ApplicationBuilder addPropertySource(String src) {
        baseProperties.add(src);
        return this;
    }

    @Override
    public ApplicationBuilder addBeanPostProcessor(Class<? extends BeanPostProcessor> bpp) {
        beanPostProcessorClasses.add(bpp);
        return this;
    }

    @Override
    public ApplicationBuilder addBeanProxyPostProcessor(Class<? extends BeanProxyPostProcessor> bpp) {
        beanProxyPostProcessorClasses.add(bpp);
        return this;
    }

    @Override
    public ApplicationBuilder setArgs(String[] args) {
        this.args = Arrays.copyOf(args, args.length);
        return this;
    }

    @Override
    public ApplicationContext build() {
        var configurationClasses = loadConfigurationClasses();
        var configurationMetadata = loadConfigurationMetadata(configurationClasses);
        var environment = loadEnvironment(configurationMetadata);

        var configurations = loadConfigurations(configurationClasses, environment);

        var beanPostProcessors = loadBeanPostProcessors(configurationMetadata);
        var beanProxyPostProcessors = loadBeanProxyPostProcessors(configurationMetadata);
        var beanDefinitions = loadBeanDefinitions(configurationMetadata, configurations);

        var beanFactory = new DefaultContextAwareBeanFactory(beanPostProcessors, beanProxyPostProcessors);

        return new CachingApplicationContext(beanDefinitions, beanFactory, environment);
    }

    private Collection<Object> loadConfigurations(Collection<Class<?>> configurationsClasses, Environment environment) {
        var configurationsPostProcessors = loadConfigurationPostProcessors().stream()
                .map(cls -> {
                    try {
                        var constructor = cls.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return configurationsClasses.stream()
                .map(cls -> {
                    try {
                        var constructor = cls.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return (Object) constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(obj -> configurationsPostProcessors.forEach(cps -> cps.configure(obj, environment)))
                .collect(Collectors.toList());
    }

    private Collection<Class<? extends ConfigurationPostProcessor>> loadConfigurationPostProcessors() {
        return new Reflections("dev.flanker.banking.infra.ioc.cpp").getSubTypesOf(ConfigurationPostProcessor.class);
    }

    private BeanDefinitionContainer loadBeanDefinitions(ConfigurationMetadata metadata, Collection<Object> configurations) {
        var containerBuilder = BeanDefinitionContainerBuilder.builder();

        for (String basePackage : basePackages) {
            new PackageBeanDefinitionScanner(basePackage).scan(containerBuilder);
        }

        for (String basePackage : metadata.packages()) {
            new PackageBeanDefinitionScanner(basePackage).scan(containerBuilder);
        }

        var configurationObjectReader = ConfigurationObjectBeanDefinitionReader.getInstance();
        for (Object configuration : configurations) {
            for (BeanDefinition beanDefinition : configurationObjectReader.read(configuration)) {
                containerBuilder.add(beanDefinition);
            }
        }

        return containerBuilder.build();
    }

    private Environment loadEnvironment(ConfigurationMetadata metadata) {
        var properties = new ConcurrentHashMap<String, String>();
        properties.putAll(new FilePropertiesLoader(baseProperties).load());
        properties.putAll(new FilePropertiesLoader(metadata.propertySources()).load());
        properties.putAll(new SystemPropertiesLoader().load());
        return Environment.createEnvironment(properties);
    }

    private ConfigurationMetadata loadConfigurationMetadata(Collection<Class<?>> configurations) {
        var metadata = new ConfigurationMetadata(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        var metadataReader = ConfigurationMetadataReader.getInstance();
        for (Class<?> configuration : configurations) {
            var configurationMetadata = metadataReader.read(configuration);
            metadata.packages().addAll(configurationMetadata.packages());
            metadata.propertySources().addAll(configurationMetadata.propertySources());
            metadata.beanPostProcessors().addAll(configurationMetadata.beanPostProcessors());
            metadata.beanProxyPostProcessor().addAll(configurationMetadata.beanProxyPostProcessor());
        }
        return metadata;
    }

    private Collection<Class<?>> loadConfigurationClasses() {
        var configurations = new HashSet<Class<?>>();
        for (Class<?> baseConfiguration : baseConfigurations) {
            configurations.addAll(new ImportAnnotationConfigurationClassScanner(baseConfiguration).scan());
        }
        return configurations;
    }

    private Collection<BeanPostProcessor> loadBeanPostProcessors(ConfigurationMetadata metadata) {
        try {
            var beanPostProcessors = new ArrayList<BeanPostProcessor>();

            var reflections = new Reflections("dev.flanker.banking.infra.ioc.bpp");
            for (Class<? extends BeanPostProcessor> bpp : reflections.getSubTypesOf(BeanPostProcessor.class)) {
                var constructor = bpp.getDeclaredConstructor();
                constructor.setAccessible(true);
                beanPostProcessors.add(constructor.newInstance());
            }

            for (Class<? extends BeanPostProcessor> bpp : beanPostProcessorClasses) {
                var constructor = bpp.getDeclaredConstructor();
                constructor.setAccessible(true);
                beanPostProcessors.add(constructor.newInstance());
            }

            for (Method bppf : metadata.beanPostProcessors()) {
                bppf.setAccessible(true);
                beanPostProcessors.add((BeanPostProcessor) bppf.invoke(null));
            }
            return beanPostProcessors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<BeanProxyPostProcessor> loadBeanProxyPostProcessors(ConfigurationMetadata metadata) {
        try {
            var beanPostProcessors = new ArrayList<BeanProxyPostProcessor>();

            var reflections = new Reflections("dev.flanker.banking.infra.ioc.bppp");
            for (Class<? extends BeanProxyPostProcessor> bppp : reflections.getSubTypesOf(BeanProxyPostProcessor.class)) {
                var constructor = bppp.getDeclaredConstructor();
                constructor.setAccessible(true);
                beanPostProcessors.add(constructor.newInstance());
            }

            for (Class<? extends BeanProxyPostProcessor> bpp : beanProxyPostProcessorClasses) {
                var constructor = bpp.getDeclaredConstructor();
                constructor.setAccessible(true);
                beanPostProcessors.add(constructor.newInstance());
            }

            for (Method bppf : metadata.beanProxyPostProcessor()) {
                bppf.setAccessible(true);
                beanPostProcessors.add((BeanProxyPostProcessor) bppf.invoke(null));
            }
            return beanPostProcessors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
