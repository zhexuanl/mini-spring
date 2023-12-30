package org.minis.beans.factory.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.minis.BeanDefinition;
import org.minis.beans.*;
import org.minis.beans.factory.BeanFactory;
import org.minis.beans.factory.config.ConstructorArgumentValue;
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


public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanDefinitionRegistry, BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    private final Logger logger = LogManager.getLogger(SimpleBeanFactory.class);

    public SimpleBeanFactory() {
    }

    /**
     * In order to reduce its internal complexity,
     * Spring provides a very important wrapper method: refresh().
     * The specific wrapper method is also very simple,
     * that is, all the Bean call once getBean(),
     * use getBean() method createBean() create Bean instances,
     * you can use only one method to container all the instances of the Bean to be created!
     */
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

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
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

    public Object getBean(String beanName) throws BeansException {
        logger.debug("Attempting to retrieve bean with name: " + beanName);

        // try to get bean instance
        Object singleton = this.getSingleton(beanName);
        // if not found bean instance, get its BeanDefinition to instantiate
        if (singleton == null) {
            // if singleton object not found, try to get from early singleton object
            logger.debug("Bean not found in singleton instances. Checking early singleton objects for bean: " + beanName);
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                logger.debug("Bean not found. Creating new bean instance for name: " + beanName);
                // if early singleton object not found, create and register bean
                final BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
                singleton = createBean(beanDefinition);
                registerBean(beanName, singleton);

                logger.debug("New bean instance created and registered for name: " + beanName);

                // reserve place for bean post processor
                logger.debug("Starting post-processing for bean: " + beanName);

                // step 1: postProcessBeforeInitialization
                // step 2: afterPropertiesSet
                // step 3: init-method
                // step 4: postProcessAfterInitialization
            }
        }
        if (singleton == null) {
            logger.error("Failed to retrieve or create bean with name: " + beanName);
            throw new BeansException("Unable to retrieve or create bean with name: " + beanName);
        }

        logger.debug("Successfully retrieved bean with name: " + beanName);
        return singleton;
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

    /**
     * @param beanDefinition
     * @return
     */
    private Object createBean(BeanDefinition beanDefinition) {
        logger.debug("Starting bean creation for ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName());
        Class<?> clz = null;
        // create raw bean
        Object obj = doCreateBean(beanDefinition);
        logger.debug("Raw bean created for ID: " + beanDefinition.getId());

        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        logger.debug("Bean ID: " + beanDefinition.getId() + " added to earlySingletonObjects");

        try {
            clz = Class.forName(beanDefinition.getClassName());
            logger.debug("Class loaded for bean ID: " + beanDefinition.getId());
        } catch (ClassNotFoundException e) {
            logger.error("Class not found for bean ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName(), e);
        }
        handleProperties(beanDefinition, clz, obj);
        return obj;
    }

    /**
     * doCreateBean creates a raw instance,
     * just calling the constructor method without property handling
     *
     * @param beanDefinition
     * @return
     */
    private Object doCreateBean(BeanDefinition beanDefinition) {
        logger.debug("Starting bean creation for ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName());
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> constructor = null;

        try {
            clz = Class.forName(beanDefinition.getClassName());

            // process constructor arguments
            ConstructorArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();

            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];

                // for each parameter, being handled separately by the data type
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ConstructorArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);

                    switch (argumentValue.getType()) {
                        case "Integer":
                        case "java.lang.Integer":
                            paramTypes[i] = Integer.class;
                            paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                            break;

                        case "int":
                            paramTypes[i] = int.class;
                            paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                            break;

                        case "String":
                        case "java.lang.String":
                        default:
                            paramTypes[i] = String.class;
                            paramValues[i] = argumentValue.getValue();
                            break;
                    }
                    logger.debug("Processing constructor argument: Type=" + argumentValue.getType() + ", Value=" + argumentValue.getValue());
                }
                try {
                    constructor = clz.getConstructor(paramTypes);
                    obj = constructor.newInstance(paramValues);
                } catch (NoSuchMethodException | InvocationTargetException | SecurityException |
                         InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            } else {
                // if it is a no argument constructor, create the instance directly
                obj = clz.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException e) {
            logger.error("Error creating bean for ID: " + beanDefinition.getId() + ", Class: " + beanDefinition.getClassName(), e);
        }
        logger.debug(String.format("%s bean created. %s: %s", beanDefinition.getId(), beanDefinition.getClassName(), obj));

        return obj;
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
                    logger.error(e);
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

//    /**
//     * Initial implementation of getBean()
//     * @param beanName
//     * @return
//     * @throws BeansException
//     */
//    @Override
//    public Object getBean(String beanName) throws BeansException {
//        // try to get bean instance
//        Object singleton = this.getSingleton(beanName);
//        // if not found bean instance, get its BeanDefinition to instantiate
//        if (singleton == null) {
//            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
//            if (beanDefinition == null) {
//                throw new BeansException(String.format("Bean name [%s] not found", beanName));
//            }
//
//            try {
//                singleton = Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance();
//            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
//                     InvocationTargetException e) {
//                e.printStackTrace();
//            }
//
//            // register the new bean instance
//            this.registerSingleton(beanName, singleton);
//        }
//        return singleton;
//    }
}
