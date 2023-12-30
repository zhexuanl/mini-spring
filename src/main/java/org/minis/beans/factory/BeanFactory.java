package org.minis.beans.factory;

import org.minis.beans.BeansException;

public interface BeanFactory {

    Object getBean(String beanName) throws BeansException;

    Boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    Class<?> getType(String name);
}
