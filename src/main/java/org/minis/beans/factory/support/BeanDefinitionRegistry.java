package org.minis.beans.factory.support;

import org.minis.BeanDefinition;

/**
 * It's like a repository for BeanDefinitions that holds, removes, gets and determines BeanDefinition objects.
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition beanDefinition);

    void removeBeanDefinition(String name);

    BeanDefinition getBeanDefinition(String name);

    boolean containsBeanDefinition(String name);
}

