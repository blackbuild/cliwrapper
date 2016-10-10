package com.blackbuild.tools.cliwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the parameter as parameter to be passed to the Groovy CliArgsBuilder.
 */
@Target({ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CliArgsParameter {

    /**
     * The name of the corresponding cli parameter. If empty, defaults to the -- or - (depending on the value of type)
     * and the name of the annotated field.
     */
    public String argument() default "";

    /**
     * If true, the value of argument is taken literally, i.e. without prepending '--' or '-'.
     */
    public ArgumentType type() default ArgumentType.LONG;

    /**
     * <p>Optional delimiter of the values for multi value parameter. If <code>""</code>, multivalues are added as multiple arguments. This
     * is only valid for collection type fields.</p>
     *
     * <p>examples:</p>
     * <ul>
     * <li>fieldname: example, values: { 'bla', blub' }, delimiter: ',' : --example bla,blub</li>
     * <li>fieldname: example, values: { 'bla', blub' }, delimiter: <code>null</code> : --example bla --example blub</li>
     * </ul>
     */
    public String delimiter() default "";

    /**
     * If the annotated field is a Map, this is used to construct key value pairs. Default is '='.
     *
     * <p>examples (argument value: entry, delimiter: <code>null</code>)</p>
     * <ul>
     * <li>values: { 'bla: bli', blub: blob' }, assignment: '=' : --entry bla=bli --entry blub=blob</li>
     * <li>values: { 'bla: bli', blub: blob' }, assignment: null : --entry bla bli --entry blub blob</li>
     * </ul>
     */
    public String assignment() default "=";
}
