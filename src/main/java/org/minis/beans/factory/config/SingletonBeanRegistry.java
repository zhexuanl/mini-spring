package org.minis.beans.factory.config;

/**
 * Standardized the methods to manage singleton bean
 */
public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    Boolean containsSingleton(String beanName);

    String[] getSingletonNames();
}
