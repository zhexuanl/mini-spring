package org.minis.context;

import org.minis.BeanDefinition;
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
public class ClassPathXmlApplicationContext {
    private final BeanFactory factory;

    /**
     * The context is responsible for integrating the startup process of the container,
     * reading the external configuration, parsing the bean definition,
     * and creating the bean factory.
     *
     * @param fileName
     */
    public ClassPathXmlApplicationContext(String fileName) {
        Resource resource = new ClassPathXmlResource(fileName);
        BeanFactory factory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(resource);
        this.factory = factory;
    }

    /**
     * The context provides a getBean for public access,
     * underneath which is the corresponding method of the called BeanFactory.
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object getBean(String beanName) throws BeansException {
        return this.factory.getBean(beanName);
    }

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.factory.registerBeanDefinition(beanDefinition);
    }


}
