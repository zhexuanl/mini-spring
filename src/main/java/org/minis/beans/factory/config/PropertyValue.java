package org.minis.beans.factory.config;

/**
 * POJO class corresponding to the &lt;property&gt; tag in beans.xml
 */
public class PropertyValue {
    private String type;
    private String name;
    private Object value;
    private boolean isRef;

    public PropertyValue(String type, String name, Object value, boolean isRef) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.isRef = isRef;
    }

    public boolean isRef() {
        return isRef;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
