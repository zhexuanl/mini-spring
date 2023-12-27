package org.minis.context;

import org.minis.beans.BeansException;
import org.minis.beans.factory.BeanFactory;
import org.minis.beans.factory.SimpleBeanFactory;
import org.minis.beans.factory.xml.XmlBeanDefinitionReader;
import org.minis.core.io.ClassPathXmlResource;
import org.minis.core.io.Resource;

/**
 * <pre>
 * 1. Parses the contents of the XML file.
 * 2. Load the parsed content and build the BeanDefinition.
 * 3. Read the configuration information of the BeanDefinition,
 *    instantiate the bean, and inject it into the BeanFactory container.
 * </pre>
 */
public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {
    SimpleBeanFactory beanFactory;

    /**
     * The context is responsible for integrating the startup process of the container,
     * reading the external configuration, parsing the bean definition,
     * and creating the bean beanFactory.
     *
     * @param fileName
     */
    public ClassPathXmlApplicationContext(String fileName) {
        Resource resource = new ClassPathXmlResource(fileName);
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }


    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {

    }
}
