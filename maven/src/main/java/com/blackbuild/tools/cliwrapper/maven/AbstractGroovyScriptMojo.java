package com.blackbuild.tools.cliwrapper.maven;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

/**
 * Helper class for a groovy script based mojo. Generates s script instance from the given script (as classloader resource),
 * converts fields of the implementing mojo (usually Parameter fields) annotated with CliBuilderParameter to an argument
 * array which is then passed to the script.
 *
 * This class contains hook methods for preParsing (before any parsing is done), adjustArguments (after parsing but before
 * the script is executed) and postRun (after the execution of the script).
 */
public abstract class AbstractGroovyScriptMojo extends AbstractCLIScriptMojo {

    @Override
    protected Object executeCLI(List<String> parsedArguments) throws MojoFailureException, MojoExecutionException {
        Script script = parseScript();
        return run(script, parsedArguments);
    }

    @SuppressWarnings("unchecked")
    private Script parseScript() throws MojoExecutionException {
        Script script;
        try {
            GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader());
            URL scriptUrl = Thread.currentThread().getContextClassLoader().getResource(getScriptName());

            if (scriptUrl == null)
                throw new FileNotFoundException("Could not find script file: " + getScriptName());

            Class<? extends Script> scriptClass = (Class<? extends Script>) loader.parseClass(new GroovyCodeSource(scriptUrl));
            script = scriptClass.newInstance();
        } catch (Exception e) {
            throw new MojoExecutionException("Error while trying to parse script.", e);
        }
        return script;
    }

    private Object run(Script script, List<String> arguments) throws MojoFailureException, MojoExecutionException {
        try {
            script.setBinding(new Binding(arguments.toArray(new String[arguments.size()])));
            return script.run();
        } catch (Exception e) {
            return handleScriptException(e);
        }
    }

    /**
     * Hook Method for Exception handling of the script. This method is called when the execution of the script
     * throws an exception. This method can be used to convert the exception to a MojoException (perhaps with
     * a custom error message). While this method will usually exit by throwing an exception, it can also be used to
     * ignore the script exception and provide an alternative result object instead.
     *
     * The default behaviour of this method is to wrap the provided exception in a MojoFailureException
     *
     * @param scriptException The exception thrown while executing the script.
     * @return An alternative result object for the script.
     * @throws MojoFailureException The wrapped scriptException.
     * @throws MojoExecutionException Can be used in subclasses to stop the mojo execution with an error.
     */
    protected Object handleScriptException(Exception scriptException) throws MojoFailureException, MojoExecutionException {
        throw new MojoFailureException("Error while trying to run script.", scriptException);
    }

    /**
     * Template method to provide the name of the script to be parsed. This must be a fully qualified classloader path,
     * for example "/com/blackbuild/demo/MyGroovyScript.groovy"
     *
     * @return The path to the script as string.
     */
    protected abstract String getScriptName();
}