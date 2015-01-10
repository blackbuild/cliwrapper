package com.blackbuild.tools.cliwrapper;

import java.util.List;

/**
 * TODO Replace with class description.
 */
public abstract class AbstractCliWrapper {

    private final Object argumentProvider;

    private CliExecutionListener listener;

    public AbstractCliWrapper(Object argumentProvider) {
        this.argumentProvider = argumentProvider;
    }

    public Object execute() throws CliBuilderException {

        preParse();
        List<String> parsedArguments = parseArgumentsFromFields();

        adjustArguments(parsedArguments);

        Object result = executeCLI(parsedArguments);

        return postRun(result);
    }

    /**
     * Abstract method used to execute the actual cli command. This is implemented by technology specific subclasses.
     * @param parsedArguments The parsed arguments.
     * @return A generic result object (can be null).
     * @throws CliBuilderException
     */
    protected abstract Object executeCLI(List<String> parsedArguments) throws CliBuilderException;

    private List<String> parseArgumentsFromFields() throws CliBuilderException {
        try {
            return new CliBuilder(argumentProvider).createArguments();
        } catch (CliBuilderException e) {
            throw new CliBuilderException("Error while building command line.", e);
        }
    }

    /**
     * Hook method called before arguments are parsed. Can be overridden to include special validations
     * or parameter conversion.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException Can be thrown to stop the execution before parsing of the parameters
     * @throws org.apache.maven.plugin.MojoFailureException Can be thrown to stop the execution before parsing of the parameters
     */
    protected void preParse() throws CliBuilderException {
        if (listener != null)
            listener.prepareParsing();
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
    protected void adjustArguments(List<String> parsedArguments) throws CliBuilderException {
        if (listener != null)
            listener.adjustArguments(parsedArguments);
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
    protected Object postRun(Object result) throws CliBuilderException {
        if (listener != null)
            return listener.adjustResult(result);
        else
            return result;
    }

    public CliExecutionListener getListener() {
        return this.listener;
    }

    public void setListener(CliExecutionListener listener) {
        this.listener = listener;
    }
}
