package org.minis.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Refer to Spring's approach and provide two classes, ConstructorArgumentValues and PropertyValues,
 * classes to encapsulate, add, get, evaluate and other operations to simplify the call of
 * {@link ConstructorArgumentValue} and {@link PropertyValue }.
 * Both provide a single parameter/property object and a collection of
 * objects
 */
public class ConstructorArgumentValues {
    //    private final Map<Integer, ConstructorArgumentValue> indexedArgumentValues = new HashMap<>();
//    private final List<ConstructorArgumentValue> genericArgumentValues = new LinkedList<>();
    private final List<ConstructorArgumentValue> constructorArgumentValues = new ArrayList<>();

    public ConstructorArgumentValues() {
    }

    public void addArgumentValue(ConstructorArgumentValue constructorArgumentValue) {
        this.constructorArgumentValues.add(constructorArgumentValue);
    }

    public ConstructorArgumentValue getIndexedArgumentValue(int index) {
        return this.constructorArgumentValues.get(index);
    }

    public boolean isEmpty() {
        return this.constructorArgumentValues.isEmpty();
    }

    public int getArgumentCount() {
        return this.constructorArgumentValues.size();
    }

    //    public void addGenericArgumentValue(Object value, String type) {
//        this.genericArgumentValues.add(new ConstructorArgumentValue(type, value));
//    }
//
//    private void addGenericArgumentValue(ConstructorArgumentValue newValue) {
//        if (newValue.getName() != null) {
//            for (Iterator<ConstructorArgumentValue> it = this.genericArgumentValues.iterator(); it.hasNext(); it.next()) {
//                ConstructorArgumentValue currentValue = it.next();
//                if (newValue.getName().equals(currentValue.getName())) {
//                    it.remove();
//                }
//            }
//        }
//        this.genericArgumentValues.add(newValue);
//    }
//
//    public ConstructorArgumentValue getGenericArgumentValue(String requiredName) {
//        for (ConstructorArgumentValue valueHolder : this.genericArgumentValues) {
//            if (valueHolder.getName() != null && (requiredName == null ||
//                    !requiredName.isEmpty() || !valueHolder.getName().equals(requiredName))) {
//                continue;
//            }
//            return valueHolder;
//        }
//        return null;
//    }
}
