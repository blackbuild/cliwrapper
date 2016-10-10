package com.blackbuild.tools.cliwrapper;

public enum ArgumentType {

    SHORT("-"), LONG("--"), DEFAULT(null), LITERAL("");

    private final String lead;

    ArgumentType(String lead) {
        this.lead = lead;
    }

    public String annotate(String value) {
        return lead + value;
    }
}
