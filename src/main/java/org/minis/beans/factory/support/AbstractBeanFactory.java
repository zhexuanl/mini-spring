package org.minis.beans.factory.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.minis.BeanDefinition;
import org.minis.beans.BeansException;
import org.minis.beans.factory.BeanFactory;
import org.minis.beans.factory.config.ConstructorArgumentValues;
import org.minis.beans.factory.config.PropertyValue;
import org.minis.beans.factory.config.PropertyValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>();

    private Logger logger = LogManager.getLogger(AbstractBeanFactory.class);

    public AbstractBeanFactory() {
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        // try to get bean instance from ioc
        Object singleton = this.singletonObjects.get(beanName);
        if (singleton == null) {
            // try to get bean from early singleton object {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                // create and register bean
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                singleton = createBean(beanDefinition);
                registerBean(beanName, singleton);
                // execute beanpostprocessor process
                // step 1: postProcessBeforeInitialization
                applyBeanPostProcessorBeforeInitialization(singleton, beanName);
                // step 2: init-method
                if (beanDefinition.getInitMethodName() != null && !beanDefinition.getInitMethodName().isEmpty()) {
                    invokeInitMethod(beanDefinition, singleton);
                }
                // step 3: postProcessAfterInitialization
                applyBeanPostProcessorAfterInitialization(singleton, beanName);
            }
        }
        return singleton;
    }

    private void invokeInitMethod(BeanDefinition beanDefinition, Object obj) {
        Class<?> clz = beanDefinition.getClass();
        Method method = null;
        try {
            method = clz.getMethod(beanDefinition.getInitMethodName());
        } catch (NoSuchMethodException e) {
            logger.error("Init method not found: " + beanDefinition.getInitMethodName(), e);
        }
        try {
            method.invoke(obj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("Error to invoke init method: " + beanDefinition.getInitMethodName(), e);
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        logger.debug("Starting bean creation for ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName());

        Class<?> clz = null;
        // create raw bean instance
        Object obj = doCreateBean(beanDefinition);
        logger.debug("Raw bean created for ID: " + beanDefinition.getId());

        // put raw bean into cache
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        logger.debug("Bean ID: " + beanDefinition.getId() + " added to earlySingletonObjects");

        try {
            clz = Class.forName(beanDefinition.getClassName());
            logger.debug("Class loaded for bean ID: " + beanDefinition.getId());

        } catch (ClassNotFoundException e) {
            logger.error("Class not found for bean ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName(), e);

        }
        populateBean(beanDefinition, clz, obj);

        return obj;
    }

    private Object doCreateBean(BeanDefinition beanDefinition) {
        Class<?> clz;
        Object obj = null;
        Constructor<?> constructor;
        try {
            clz = Class.forName(beanDefinition.getClassName());
            // handle constructor
            ConstructorArgumentValues cav = beanDefinition.getConstructorArgumentValues();
            if (!cav.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[cav.getArgumentCount()];
                Object[] paramValues = new Object[cav.getArgumentCount()];

                for (int i = 0; i < cav.getArgumentCount(); i++) {
                    if ("String".equals(cav.getIndexedArgumentValue(i).getType()) || "java.lang.String".equals(cav.getIndexedArgumentValue(i).getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = cav.getIndexedArgumentValue(i).getValue();
                    } else if ("Integer".equals(cav.getIndexedArgumentValue(i).getType()) || "java.lang.Integer".equals(cav.getIndexedArgumentValue(i).getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) cav.getIndexedArgumentValue(i).getValue());
                    } else if ("int".equals(cav.getIndexedArgumentValue(i).getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf(((String) cav.getIndexedArgumentValue(i).getValue()));
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = cav.getIndexedArgumentValue(i).getValue();
                    }

                }
                try {
                    constructor = clz.getConstructor(paramTypes);
                    obj = constructor.newInstance(paramValues);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    logger.error("Error creating bean for ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName(), e);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        logger.debug(String.format("%s bean created. %s: %s", beanDefinition.getId(), beanDefinition.getClassName(), obj));

        return obj;
    }

    private void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        handleProperties(beanDefinition, clz, obj);
    }

    private void handleProperties(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        // process property
        logger.debug("Starting to handle properties for bean ID: {} ", beanDefinition.getId());
        PropertyValues propertyValues = beanDefinition.getPropertyValues();

        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValues().get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();

                logger.debug("Processing property - Name: " + pName + ", Type: " + pType + ", Value: " + pValue + ", isRef: " + isRef);

                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];

                if (!isRef) {
                    switch (pType) {
                        case "String":
                        case "java.lang.String":
                        default:
                            paramTypes[0] = String.class;
                            break;

                        case "Integer":
                        case "java.lang.Integer":
                            paramTypes[0] = Integer.class;
                            break;

                        case "int":
                            paramTypes[0] = int.class;
                            break;
                    }
                    paramValues[0] = pValue;
                } else {
                    // is ref, create dependent beans
                    logger.debug("Handling reference type for property: " + pName + ", referring to bean ID: " + pValue);
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        logger.error("Class not found for property: " + pName + ", Type: " + pType, e);

                    }

                    try {
                        paramValues[0] = getBean((String) pValue);
                    } catch (BeansException e) {
                        logger.error("Bean exception occurred for reference: " + pValue, e);
                    }
                }
                // Look up the setter method according to the setXxxx specification and call the setter method to set the property
                String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                logger.debug("Looking up setter method: " + methodName + " for property: " + pName);

                Method method = null;
                try {
                    assert clz != null;
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException | SecurityException e) {
                    logger.error("Method not found " + methodName + " in class " + pType, e);
                }
                try {
                    assert method != null;
                    method.invoke(obj, paramValues);
                    logger.debug("Property " + pName + " set successfully using method: " + methodName);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("Error setting property " + pName + " using method: " + methodName, e);
                }
            }
        }
        logger.debug("Completed handling properties for bean ID: " + beanDefinition.getId());
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
//        if (!beanDefinition.isLazyInit()) {
//            try {
//                getBean(name);
//            } catch (BeansException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return (Class<?>) beanDefinitionMap.get(name).getBeanClass();
    }

    abstract public Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName) throws BeansException;

    abstract public Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) throws BeansException;
}
