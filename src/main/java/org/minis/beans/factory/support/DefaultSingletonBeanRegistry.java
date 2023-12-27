package org.minis.beans.factory.support;

import org.minis.beans.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    protected final List<String> beanNames = new ArrayList<>();
    protected final Map<String, Object> singletons = new ConcurrentHashMap<>(256);

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        // for thread safety in multi-threaded environment
        synchronized (this.singletons) {
            // double check lock
            Object object = this.singletons.get(beanName);
            if (object != null) {
                throw new IllegalStateException(String.format("Could not register object %s under bean name %s. Object already registered before", object, beanName));
            }
            singletons.put(beanName, singletonObject);
            beanNames.add(beanName);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.singletons.get(beanName);
    }

    @Override
    public Boolean containsSingleton(String beanName) {
        return this.singletons.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletons) {
            singletons.remove(beanName);
            beanNames.remove(beanName);
        }
    }
}
