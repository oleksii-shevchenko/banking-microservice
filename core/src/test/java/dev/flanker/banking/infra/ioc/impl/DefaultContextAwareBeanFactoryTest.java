package dev.flanker.banking.infra.ioc.impl;

import dev.flanker.banking.infra.ioc.annotation.AnnotatedClassBeanDefinitionReader;
import org.junit.jupiter.api.Test;

class DefaultContextAwareBeanFactoryTest {
    private final DefaultContextAwareBeanFactory beanFactory = new DefaultContextAwareBeanFactory();

    @Test
    public void beanFactoryTest() {
        var bean = beanFactory.createBean(new AnnotatedClassBeanDefinitionReader().read(Parent.class));
    }

}

class Parent {
    public Parent() {
        System.out.println("In Parent Constructor");
    }
}
