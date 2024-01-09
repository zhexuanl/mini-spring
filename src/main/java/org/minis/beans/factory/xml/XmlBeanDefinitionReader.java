package org.minis.beans.factory.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.minis.BeanDefinition;
import org.minis.beans.factory.config.ConstructorArgumentValue;
import org.minis.beans.factory.config.ConstructorArgumentValues;
import org.minis.beans.factory.config.PropertyValue;
import org.minis.beans.factory.config.PropertyValues;
import org.minis.beans.factory.support.AutowiredCapableBeanFactory;
import org.minis.core.io.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Convert parsed XML into the required BeanDefinition.
 */
public class XmlBeanDefinitionReader {
    AutowiredCapableBeanFactory beanFactory;

    private Logger logger = LogManager.getLogger(XmlBeanDefinitionReader.class);

    public XmlBeanDefinitionReader(AutowiredCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Converts the parsed
     * XML content from beans.xml into a BeanDefinition and loads it into the BeanFactory.
     *
     * @param resource
     */
    public void loadBeanDefinitions(Resource resource) {
        logger.debug("Starting loadBeanDefinitions.");
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            logger.debug("Processing resource: " + element.getPath());

            String beanId = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");

            final BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);

            logger.debug("Created BeanDefinition with id: " + beanId + " and class: " + beanClassName);

            // process <constructor-arg>: in beans.xml
            List<Element> constructorElements = element.elements("constructor-arg");
            ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
            for (Element e : constructorElements) {
                String aType = e.attributeValue("type");
                String aName = e.attributeValue("name");
                String aValue = e.attributeValue("value");

                logger.debug("Processing constructor argument: " + aName + " for bean: " + beanId);
                argumentValues.addArgumentValue(new ConstructorArgumentValue(aType, aName, aValue));
            }
            beanDefinition.setConstructorArgumentValues(argumentValues);

            // process <property> tag in beans.xml
            List<Element> propertyElements = element.elements("property");
            PropertyValues propertyValues = new PropertyValues();
            List<String> refs = new ArrayList<>();

            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                String pRef = e.attributeValue("ref");
                String pV = "";
                boolean isRef = false;

                logger.debug("Processing property: " + pName + " for bean: " + beanId);

                if (pValue != null && !pValue.isEmpty()) {
                    pV = pValue;
                } else if (pRef != null && !pRef.isEmpty()) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                propertyValues.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(propertyValues);
            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);

            logger.debug("Bean " + beanId + " depends on: " + Arrays.toString(refArray));

            this.beanFactory.registerBeanDefinition(beanId, beanDefinition);
            logger.debug("Registered BeanDefinition with id: " + beanId);
        }
        logger.debug("Finished loadBeanDefinitions.");
    }
}
