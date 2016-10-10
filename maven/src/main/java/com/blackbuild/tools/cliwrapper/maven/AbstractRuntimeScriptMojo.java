package com.blackbuild.tools.cliwrapper.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

public class AbstractRuntimeScriptMojo extends AbstractCLIScriptMojo {

    @Override
    protected Object executeCLI(List<String> parsedArguments) throws MojoFailureException, MojoExecutionException {
        throw new UnsupportedOperationException("not yet implemented, sorry");
    }
}
