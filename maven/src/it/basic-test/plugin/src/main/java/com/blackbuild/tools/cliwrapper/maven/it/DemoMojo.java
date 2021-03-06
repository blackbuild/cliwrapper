package com.blackbuild.tools.cliwrapper.maven.it;

import com.blackbuild.tools.cliwrapper.ArgumentType;
import com.blackbuild.tools.cliwrapper.CliArgsParameter;
import com.blackbuild.tools.cliwrapper.maven.AbstractGroovyScriptMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
@Mojo(name = "test", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class DemoMojo extends AbstractGroovyScriptMojo {

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    @CliArgsParameter(type = ArgumentType.DEFAULT)
    private File outputDirectory;

    @Parameter
    @CliArgsParameter
    private String filter;

    @Parameter
    @CliArgsParameter(argument = "include", delimiter = ",")
    private String[] includes;

    @Parameter
    @CliArgsParameter
    private Map<String, String> properties;

    @Override
    protected String getScriptName() {
        return "Test.groovy";
    }
}