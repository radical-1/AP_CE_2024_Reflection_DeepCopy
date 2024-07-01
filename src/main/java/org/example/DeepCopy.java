package org.example;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.reflect.Modifier.isStatic;

public class DeepCopy {

    public Object clone(Object object) throws Exception {
        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();
        if (clazz.isPrimitive() || clazz.equals(String.class) || Number.class.isAssignableFrom(clazz) || Boolean.class.equals(clazz)) {
            return object;
        }

        if (object instanceof ArrayList) {
            return cloneArrayList((ArrayList) object);
        }
        if(clazz.isArray()){
            return cloneArray(object, clazz);
        }
        Object copy = clazz. getConstructor().newInstance();
        ArrayList<Field> fields = new ArrayList<>();
        while (!clazz.equals(Object.class)) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                if (field.getType().isPrimitive() || field.getType().equals(String.class) || Number.class.isAssignableFrom(field.getType()) || Boolean.class.equals(field.getType())) {
                    field.set(copy, field.get(object));
                } else {
                    Object fieldObject = field.get(object);
                    if (fieldObject == null) {
                        field.set(copy, null);
                    } else {
                        field.set(copy, clone(fieldObject));
                    }
                }
                field.setAccessible(isAccessible);
            }
        }
        return copy;
    }

    private ArrayList cloneArrayList(ArrayList original) throws Exception {
        ArrayList copy = new ArrayList(original.size());
        for (Object element : original) {
            copy.add(clone(element));
        }
        return copy;
    }
    private Object cloneArray(Object object, Class<?> clazz) throws Exception {
        int length = Array.getLength(object);
        Object copy = Array.newInstance(clazz.getComponentType(), length);
        for (int i = 0; i < length; i++) {
            Array.set(copy, i, clone(Array.get(object, i)));
        }
        return copy;
    }
}