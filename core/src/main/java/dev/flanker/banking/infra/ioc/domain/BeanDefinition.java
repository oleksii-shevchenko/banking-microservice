package dev.flanker.banking.infra.ioc.domain;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public record BeanDefinition(
        String id,
        Class<?> beanClass,
        Set<Class<?>> interfaces,
        ConstructorDefinition constructorDefinition,
        boolean eagerCreated,
        List<String> preDestroyMethods,
        List<String> postConstructMethods
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String beanId;
        private Class<?> beanClass;
        private Set<Class<?>> beanInterfaces;
        private Function<Object[], Object> beanConstructor;
        private Class<?>[] beanConstructorParametersTypes;
        private Annotation[][] beanConstructorParametersAnnotations;
        private int beanConstructorParametersCount;
        private boolean eagerCreated;
        private List<String> preDestroyMethods;
        private List<String> postConstructMethods;

        public String getBeanId() {
            return beanId;
        }

        public Builder setBeanId(String beanId) {
            this.beanId = beanId;
            return this;
        }

        public Class<?> getBeanClass() {
            return beanClass;
        }

        public Builder setBeanClass(Class<?> beanClass) {
            this.beanClass = beanClass;
            return this;
        }

        public Set<Class<?>> getBeanInterfaces() {
            return beanInterfaces;
        }

        public Builder setBeanInterfaces(Set<Class<?>> beanInterfaces) {
            this.beanInterfaces = beanInterfaces;
            return this;
        }

        public Function<Object[], Object> getBeanConstructor() {
            return beanConstructor;
        }

        public Builder setBeanConstructor(Function<Object[], Object> beanConstructor) {
            this.beanConstructor = beanConstructor;
            return this;
        }

        public Class<?>[] getBeanConstructorParametersTypes() {
            return beanConstructorParametersTypes;
        }

        public Builder setBeanConstructorParametersTypes(Class<?>[] beanConstructorParametersTypes) {
            this.beanConstructorParametersTypes = beanConstructorParametersTypes;
            return this;
        }

        public Annotation[][] getBeanConstructorParametersAnnotations() {
            return beanConstructorParametersAnnotations;
        }

        public Builder setBeanConstructorParametersAnnotations(Annotation[][] beanConstructorParametersAnnotations) {
            this.beanConstructorParametersAnnotations = beanConstructorParametersAnnotations;
            return this;
        }

        public int getBeanConstructorParametersCount() {
            return beanConstructorParametersCount;
        }

        public Builder setBeanConstructorParametersCount(int beanConstructorParametersCount) {
            this.beanConstructorParametersCount = beanConstructorParametersCount;
            return this;
        }

        public boolean isEagerCreated() {
            return eagerCreated;
        }

        public Builder setEagerCreated(boolean eagerCreated) {
            this.eagerCreated = eagerCreated;
            return this;
        }

        public List<String> getPreDestroyMethods() {
            return preDestroyMethods;
        }

        public Builder setPreDestroyMethods(List<String> preDestroyMethods) {
            this.preDestroyMethods = preDestroyMethods;
            return this;
        }

        public List<String> getPostConstructMethods() {
            return postConstructMethods;
        }

        public Builder setPostConstructMethods(List<String> postConstructMethods) {
            this.postConstructMethods = postConstructMethods;
            return this;
        }

        public BeanDefinition build() {
            return new BeanDefinition(
                    beanId,
                    beanClass,
                    beanInterfaces,
                    new ConstructorDefinition(
                            beanConstructor,
                            beanConstructorParametersTypes,
                            beanConstructorParametersAnnotations,
                            beanConstructorParametersCount
                    ),
                    eagerCreated,
                    preDestroyMethods != null ? preDestroyMethods : emptyList(),
                    postConstructMethods != null ? postConstructMethods : emptyList()
            );
        }
    }
}
