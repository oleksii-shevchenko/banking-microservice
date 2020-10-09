package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.BeanDefinitionContainerBuilder;
import dev.flanker.banking.infra.ioc.BeanDefinitionScanner;
import jakarta.inject.Singleton;
import org.reflections.Reflections;

public class PackageBeanDefinitionScanner implements BeanDefinitionScanner {
    private final Reflections reflections;

    private final ClassBeanDefinitionReader classBeanDefinitionReader;

    public PackageBeanDefinitionScanner(String basePackage, ClassBeanDefinitionReader beanDefinitionReader) {
        this.reflections = new Reflections(basePackage);
        this.classBeanDefinitionReader = beanDefinitionReader;
    }

    @Override
    public BeanDefinitionContainerBuilder scan(BeanDefinitionContainerBuilder builder) {
        for (Class<?> bean : reflections.getTypesAnnotatedWith(Singleton.class, true)) {
            builder.add(classBeanDefinitionReader.read(bean));
        }
        return builder;
    }
}
