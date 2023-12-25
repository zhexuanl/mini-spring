package org.minis.beans.factory.xml;

import org.dom4j.Element;
import org.minis.BeanDefinition;
import org.minis.beans.factory.BeanFactory;
import org.minis.core.io.Resource;

/**
 * Convert parsed XML into the required BeanDefinition.
 */
public class XmlBeanDefinitionReader {
    private final BeanFactory beanFactory;

    public XmlBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Converts the parsed
     * XML content into a BeanDefinition and loads it into the BeanFactory.
     *
     * @param resource
     */
    public void loadBeanDefinitions(@org.jetbrains.annotations.NotNull Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanId = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
            this.beanFactory.registerBeanDefinition(beanDefinition);
        }
    }
}
