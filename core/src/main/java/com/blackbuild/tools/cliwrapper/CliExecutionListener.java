package com.blackbuild.tools.cliwrapper;

import java.util.List;

/**
 * TODO Replace with class description.
 */
public interface CliExecutionListener {

    void prepareParsing();

    void adjustArguments(List<String> parsedArguments);

    Object adjustResult(Object result);

    Object handleScriptException();

}
