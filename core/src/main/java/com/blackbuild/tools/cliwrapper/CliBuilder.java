package com.blackbuild.tools.cliwrapper;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to create a Cli command line out of an annotated object.
 */
public class CliBuilder {

    private final Object bean;
    private List<CliField> foundCliFields;
    private List<CliField> foundDefaultArguments;
    private List<String> argumentList;

    public CliBuilder(Object bean) {
        this.bean = bean;
        foundCliFields = new ArrayList<CliField>();
        foundDefaultArguments = new ArrayList<CliField>();
        determineCliFields();
    }

    public List<String> createArguments() throws CliBuilderException {

        argumentList = new LinkedList<String>();

        addNonDefaultArguments();
        addDefaultArgument();

        return argumentList;
    }

    private void addDefaultArgument() throws CliBuilderException {
        CliField defaultArgument = null;
        for (CliField cliField : foundDefaultArguments) {
            if (cliField.getRawValue() == null) continue;
            if (defaultArgument == null) {
                argumentList.addAll(cliField.getCliFragment());
                defaultArgument = cliField;
            } else {
                throw new CliBuilderException(String.format("Only one default argument allowed! (%s and %s both define one)",
                        defaultArgument.getField(), cliField.getField()));
            }
        }
    }

    private void addNonDefaultArguments() throws CliBuilderException {
        Map<String, CliField> fields = new LinkedHashMap<String, CliField>();
        for (CliField currentField : foundCliFields) {
            if (currentField.getRawValue() == null) continue;
            argumentList.addAll(currentField.getCliFragment());
            if (fields.containsKey(currentField.getArgument()))
                throw new CliBuilderException(String.format(
                        "Found multiple definitions for argument '%s' (%s and %s)", currentField.getArgument(),
                        fields.get(currentField.getArgument()).getField(), currentField.getField()));
            fields.put(currentField.getArgument(), currentField);
        }
    }

    private void determineCliFields() {
        for (AccessibleObject member : getAnnotatedMembers()) {
            CliField next = member instanceof Field ? new CliField(bean, (Field) member) : new CliField(bean,
                    (Method) member);

            if (next.isDefaultArgument()) {
                foundDefaultArguments.add(next);
            } else {
                foundCliFields.add(next);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Set<AccessibleObject> getAnnotatedMembers() {
        Set<AccessibleObject> annotatedMembers = new LinkedHashSet<AccessibleObject>();
        annotatedMembers.addAll(getAllFields(bean.getClass(), withAnnotation(CliBuilderParameter.class)));
        annotatedMembers.addAll(getAllMethods(bean.getClass(), withAnnotation(CliBuilderParameter.class)));
        return annotatedMembers;
    }

}
