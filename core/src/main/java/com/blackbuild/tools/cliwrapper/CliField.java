package com.blackbuild.tools.cliwrapper;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Collections.singletonList;

/**
 * TODO Replace with class description.
 */
class CliField {

    private static final String SINGLE_SPACE = " ";

    private final Object bean;

    private final Field field;

    private final CliArgsParameter annotation;

    private final Method method;

    private final Object rawValue;

    CliField(Object bean, Field field) {
        this.bean = bean;
        this.field = field;
        field.setAccessible(true);
        this.method = null;
        this.annotation = field.getAnnotation(CliArgsParameter.class);

        try {
            rawValue = field.get(this.bean);
        } catch (Exception e) {
            throw new IllegalStateException("Could not obtain value for " + getFieldOrMethod(), e);
        }
    }

    CliField(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.field = null;
        this.annotation = method.getAnnotation(CliArgsParameter.class);

        try {
            rawValue = method.invoke(this.bean);
        } catch (Exception e) {
            throw new IllegalStateException("Could not obtain value for " + getFieldOrMethod(), e);
        }
    }

    @Override
    public String toString() {
        if (field != null) return field.toString();
        return method.toString();
    }

    boolean isDefaultArgument() {
        return annotation.type() == ArgumentType.DEFAULT;
    }

    Object getRawValue() {
        return this.rawValue;
    }

    String getArgument() {
        String argument = annotation.argument();

        if (isDefaultArgument())
            return StringUtils.isEmpty(argument) ? null : argument;

        if (StringUtils.isEmpty(argument))
            return annotation.type().annotate(getField().getName());

        return annotation.type().annotate(argument);
    }

    String getDelimiter() {
        return annotation.delimiter();
    }

    List<String> getValueAsStringList() {
        if (getField().getType().isArray())
            return arrayValueAsStringList();
        if (Collection.class.isAssignableFrom(getField().getType()))
            return collectionValueAsStringList();
        if (Map.class.isAssignableFrom(getField().getType()))
            return mapValueAsStringList();

        return singleValueAsStringList();
    }

    private List<String> singleValueAsStringList() {
        return singletonList(String.valueOf(rawValue));
    }

    private List<String> mapValueAsStringList()  {
        Map<?, ?> rawMap = (Map<?, ?>) rawValue;

        List<String> result = new ArrayList<String>(rawMap.size());

        for (Map.Entry<?, ?> next : rawMap.entrySet()) {
            if (annotation.assignment().equals(SINGLE_SPACE)) {
                result.add(String.valueOf(next.getKey()));
                result.add(String.valueOf(next.getValue()));
            } else {
                result.add(String.valueOf(next.getKey()) + annotation.assignment() + next.getValue());
            }
        }

        return result;
    }

    private List<String> collectionValueAsStringList() {
        Collection<?> rawCollection = (Collection<?>) rawValue;

        List<String> result = new ArrayList<String>(rawCollection.size());

        for (Object next : rawCollection) {
            result.add(String.valueOf(next));
        }

        return result;
    }

    private List<String> arrayValueAsStringList() {
        Object[] rawArray = (Object[]) rawValue;
        List<String> result = new ArrayList<String>(rawArray.length);

        for (Object next : rawArray) {
            result.add(String.valueOf(next));
        }

        return result;
    }

    List<String> getCliFragment() {
        if (rawValue == null)
            return Collections.emptyList();

        List<String> rawValues = getValueAsStringList();

        String argument = getArgument();

        if (argument == null) {
            if (StringUtils.isBlank(getDelimiter()))
                return rawValues;
            else
                return singletonList(StringUtils.join(rawValues, getDelimiter()));
        }

        if (Map.class.isAssignableFrom(getField().getType()) && annotation.assignment().equals(SINGLE_SPACE)) {
            List<String> result = new ArrayList<String>();
            // map needs special Handling
            for (Iterator<String> it = rawValues.iterator(); it.hasNext();) {
                result.add(argument);
                result.add(it.next());
                result.add(it.next());
            }
            return result;
        }

        if (StringUtils.isEmpty(getDelimiter())) {
            List<String> result = new ArrayList<String>();
            for (String value : rawValues) {
                result.add(argument);
                result.add(value);
            }
            return result;
        } else if (getDelimiter().equals(SINGLE_SPACE)) {
            List<String> result = new ArrayList<String>();
            result.add(argument);
            result.addAll(rawValues);
            return result;
        } else {
            List<String> result = new ArrayList<String>();
            result.add(argument);
            result.add(StringUtils.join(rawValues, getDelimiter()));
            return result;
        }    }

    private AccessibleObject getFieldOrMethod() {
        return field != null ? field : method;
    }

    public Field getField() {
        return this.field;
    }
}