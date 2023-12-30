package org.minis.beans.factory.support;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.minis.beans.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    protected final List<String> beanNames = new ArrayList<>();
    protected final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    private final Logger logger = LogManager.getLogger(DefaultSingletonBeanRegistry.class);

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        // for thread safety in multi-threaded environment
        synchronized (this.singletonObjects) {
            // double check lock
            Object object = this.singletonObjects.get(beanName);
            if (object != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + object + "] bound");
            }
            this.singletonObjects.put(beanName, singletonObject);
            this.beanNames.add(beanName);

            logger.debug("Register bean [{}] singleton object {}", beanName, singletonObject);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }

    @Override
    public Boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            singletonObjects.remove(beanName);
            beanNames.remove(beanName);
        }
    }
}
