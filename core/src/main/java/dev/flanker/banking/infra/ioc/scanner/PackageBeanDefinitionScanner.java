package dev.flanker.banking.infra.ioc.scanner;

import dev.flanker.banking.infra.ioc.BeanDefinitionContainerBuilder;
import dev.flanker.banking.infra.ioc.BeanDefinitionScanner;
import dev.flanker.banking.infra.ioc.annotation.AnnotatedClassBeanDefinitionReader;
import dev.flanker.banking.infra.ioc.annotation.ClassBeanDefinitionReader;
import jakarta.inject.Singleton;
import org.reflections.Reflections;

public class PackageBeanDefinitionScanner implements BeanDefinitionScanner {
    private final Reflections reflections;

    private final ClassBeanDefinitionReader classBeanDefinitionReader;

    public PackageBeanDefinitionScanner(String basePackage) {
        this(basePackage, AnnotatedClassBeanDefinitionReader.getInstance());
    }

    public PackageBeanDefinitionScanner(String basePackage, ClassBeanDefinitionReader beanDefinitionReader) {
        this.reflections = new Reflections(basePackage);
        this.classBeanDefinitionReader = beanDefinitionReader;
    }

    @Override
    public BeanDefinitionContainerBuilder scan(BeanDefinitionContainerBuilder builder) {
        for (Class<?> bean : reflections.getTypesAnnotatedWith(Singleton.class)) {
            builder.add(classBeanDefinitionReader.read(bean));
        }
        return builder;
    }
}
