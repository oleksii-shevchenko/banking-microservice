package dev.flanker.banking.infra.ioc.annotation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotatedClassBeanDefinitionReaderTest {
    private final AnnotatedClassBeanDefinitionReader bdr = new AnnotatedClassBeanDefinitionReader();

    @Test
    public void bdrTest() {
        System.out.println(bdr.read(Child.class));
    }
}

class Parent implements ParentInterface {
    @PostConstruct
    public void parentPostConstruct() {}

    @PreDestroy
    public void parentPreDestroy() {}

    @Override
    public void close() throws IOException {}
}

interface ParentInterface extends AutoCloseable {
    @PostConstruct
    default void interfacePostConstruct() {}
}

@Named("containerBean")
@Singleton
class Child extends Parent implements ParentInterface {
    private Child(double d) {}

    @Inject
    private Child(String s, int i) {}

    @PostConstruct
    private void childPostConstruct() {}

    @PreDestroy
    private void childPreDestroy() {}
}
