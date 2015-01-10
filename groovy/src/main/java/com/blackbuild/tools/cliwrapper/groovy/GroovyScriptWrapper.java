package com.blackbuild.tools.cliwrapper.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;

import java.net.URL;
import java.util.List;

import com.blackbuild.tools.cliwrapper.AbstractCliWrapper;
import com.blackbuild.tools.cliwrapper.CliBuilderException;

/**
 * TODO Replace with class description.
 */
public class GroovyScriptWrapper extends AbstractCliWrapper {

    private final URL scriptUrl;

    public GroovyScriptWrapper(Object argumentProvider, URL scriptUrl) {
        super(argumentProvider);
        this.scriptUrl = scriptUrl;
    }

    @Override
    protected Object executeCLI(List<String> parsedArguments) throws CliBuilderException {
        Script script = parseScript();
        return run(script, parsedArguments);
    }

    @SuppressWarnings("unchecked")
    private Script parseScript() throws CliBuilderException {
        Script script;
        try {
            GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader());
            Class<? extends Script> scriptClass = (Class<? extends Script>) loader.parseClass(new GroovyCodeSource(scriptUrl));
            script = scriptClass.newInstance();
        } catch (Exception e) {
            throw new CliBuilderException("Error while trying to parse script.", e);
        }
        return script;
    }

    private Object run(Script script, List<String> arguments) throws CliBuilderException {
        try {
            script.setBinding(new Binding(arguments.toArray(new String[arguments.size()])));
            return script.run();
        } catch (Exception e) {
            return handleScriptException(e);
        }
    }

    private Object handleScriptException(Exception e) throws CliBuilderException {
        if (getListener() != null)
            return getListener().handleScriptException();

        throw new CliBuilderException("Exception while running command", e);
    }
}
