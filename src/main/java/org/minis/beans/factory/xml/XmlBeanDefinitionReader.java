package org.minis.beans.factory.xml;

import org.dom4j.Element;
import org.minis.beans.factory.SimpleBeanFactory;
import org.minis.core.io.Resource;

/**
 * Convert parsed XML into the required BeanDefinition.
 */
public class XmlBeanDefinitionReader {
    SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    /**
     * Converts the parsed
     * XML content into a BeanDefinition and loads it into the BeanFactory.
     *
     * @param resource
     */
    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanId = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            simpleBeanFactory.registerBean(beanId, beanClassName);
        }
    }
}
