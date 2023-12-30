package org.minis.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

public class PropertyValues {
    private final List<PropertyValue> propertyValues;

    public PropertyValues() {
        this.propertyValues = new ArrayList<>(10);
    }

    public List<PropertyValue> getPropertyValues() {
        return this.propertyValues;
    }

    public int size() {
        return this.propertyValues.size();
    }

    public void addPropertyValue(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }

//    public void addPropertyValue(String propertyType, String propertyName, Object propertyValue) {
//        addPropertyValue(new PropertyValue(propertyType, propertyName, propertyValue));
//    }

    public void removePropertyValue(PropertyValue propertyValue) {
        this.propertyValues.remove(propertyValue);
    }

    public void removePropertyValue(String propertyName) {
        this.propertyValues.remove(this.getPropertyValue(propertyName));
    }

    private PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue propertyValue : propertyValues) {
            if (propertyValue.getName().equals(propertyName)) {
                return propertyValue;
            }
        }
        return null;
    }

    public Object get(String propertyName) {
        PropertyValue propertyValue = getPropertyValue(propertyName);
        return propertyValue != null ? propertyValue.getValue() : null;
    }

    public boolean contains(String propertyName) {
        return this.getPropertyValue(propertyName) != null;
    }

    public boolean isEmpty() {
        return this.propertyValues.isEmpty();
    }
}
