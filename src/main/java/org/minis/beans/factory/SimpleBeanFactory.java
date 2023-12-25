package org.minis.beans.factory;

import org.minis.BeanDefinition;
import org.minis.beans.BeansException;
import org.minis.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleBeanFactory implements BeanFactory {
    private final List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private final List<String> beanNames = new ArrayList<>();
    private final Map<String, Object> singletons = new HashMap<>();

    @Override
    public Object getBean(String beanName) throws BeansException {
        // try to get bean instance
        Object singleton = singletons.get(beanName);
        // if not found bean instance, get its BeanDefinition to instantiate
        if (singleton == null) {
            int i = beanNames.indexOf(beanName);
            if (i == -1) {
                throw new BeansException("Bean " + beanName + " not exist");
            } else {
                BeanDefinition beanDefinition = beanDefinitions.get(i);
                try {
                    singleton = Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                singletons.put(beanDefinition.getId(), singleton);
            }
        }
        return singleton;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanNames.add(beanDefinition.getId());
        this.beanDefinitions.add(beanDefinition);
    }
}
