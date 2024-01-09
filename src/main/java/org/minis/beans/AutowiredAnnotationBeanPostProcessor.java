package org.minis.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.minis.beans.factory.annotation.Autowired;
import org.minis.beans.factory.support.AutowiredCapableBeanFactory;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private AutowiredCapableBeanFactory beanFactory;

    private Logger logger = LogManager.getLogger(AutowiredAnnotationBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clz = bean.getClass();
        Field[] fields = clz.getDeclaredFields();
        // evaluate properties in class, process property with @Autowired
        for (Field field : fields) {
            boolean isAutowired = field.isAnnotationPresent(Autowired.class);
            if (isAutowired) {
                String fieldName = field.getName();
                Object autowiredObj = this.beanFactory.getBean(fieldName);
                // inject property value
                try {
                    field.setAccessible(true);
                    field.set(bean, autowiredObj);
                } catch (IllegalAccessException e) {
                    logger.error("Fail to autowire {} for bean {}", fieldName, beanName, e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public AutowiredCapableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void setBeanFactory(AutowiredCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
