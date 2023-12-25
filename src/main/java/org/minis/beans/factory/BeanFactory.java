package org.minis.beans.factory;

import org.minis.BeanDefinition;
import org.minis.beans.BeansException;

public interface BeanFactory {

    Object getBean(String beanName) throws BeansException;
    void registerBeanDefinition(BeanDefinition beanDefinition);
}
