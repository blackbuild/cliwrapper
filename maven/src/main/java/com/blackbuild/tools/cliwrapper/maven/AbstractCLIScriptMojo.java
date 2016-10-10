package com.blackbuild.tools.cliwrapper.maven;

import com.blackbuild.tools.cliwrapper.CliArgsBuilder;
import com.blackbuild.tools.cliwrapper.CliArgsBuilderException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

/**
 * Helper class for a groovy script based mojo. Generates s script instance from the given script (as classloader resource),
 * converts fields of the implementing mojo (usually Parameter fields) annotated with CliArgsParameter to an argument
 * array which is then passed to the script.
 *
 * This class contains hook methods for preParsing (before any parsing is done), adjustArguments (after parsing but before
 * the script is executed) and postRun (after the execution of the script).
 */
public abstract class AbstractCLIScriptMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {

        preParse();
        List<String> parsedArguments = parseArgumentsFromFields();
        adjustArguments(parsedArguments);

        getLog().debug("Parsed arguments: " + parsedArguments);
        getLog().info("Executing script");
        Object result = executeCLI(parsedArguments);

        getLog().debug("Script result: " + result);

        postRun(result);
    }

    /**
     * Abstract method used to execute the actual cli command. This is overridden by technology specific subclasses.
     * @param parsedArguments The parsed arguments.
     * @return A generic result object (can be null).
     */
    protected abstract Object executeCLI(List<String> parsedArguments) throws MojoFailureException, MojoExecutionException;

    private List<String> parseArgumentsFromFields() throws MojoExecutionException {
        try {
            return new CliArgsBuilder(this).createArguments();
        } catch (CliArgsBuilderException e) {
            throw new MojoExecutionException("Error while building command line.", e);
        }
    }

    /**
     * Hook method called before arguments are parsed. Can be overridden to include special validations
     * or parameter conversion.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException Can be thrown to stop the execution before parsing of the parameters
     * @throws org.apache.maven.plugin.MojoFailureException Can be thrown to stop the execution before parsing of the parameters
     */
    protected void preParse() throws MojoExecutionException, MojoFailureException{
        // Hook Method
    }

    /**
     * Hook method called after the parameters have been parsed. Can be used to validate the parsed parameter
     * list. This method CAN be used to further modify the argument list.
     *
     * @param parsedArguments The list of parsed arguments as (modifiable) list.
     * @throws org.apache.maven.plugin.MojoExecutionException Can be thrown to stop the execution before execution of the script
     * @throws org.apache.maven.plugin.MojoFailureException Can be thrown to stop the execution before execution of the script
     */
    @SuppressWarnings("UnusedParameters")
    protected void adjustArguments(List<String> parsedArguments) throws MojoExecutionException, MojoFailureException{
        // Hook Method
    }

    /**
     * Hook method to be run after successful completion of the script. Can be for further action or to mark the
     * execution as failure as derived from the result object of the script.
     *
     * @param result The result of the executed script. This can be the evaluation of the last statement or an explicit
     *               return value
     * @throws org.apache.maven.plugin.MojoExecutionException Can be thrown to mark this execution as error
     * @throws org.apache.maven.plugin.MojoFailureException Can be thrown to mark this execution as failure
     */
    @SuppressWarnings("UnusedParameters")
    protected void postRun(Object result) throws MojoExecutionException, MojoFailureException {
        // Hook Method
    }

}