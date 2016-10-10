package com.blackbuild.tools.cliwrapper
import com.blackbuild.tools.cliwrapper.otherpackage.TestCliBean
import spock.lang.Specification

import static java.util.regex.Pattern.quote

class CliArgsBuilderSpockTest extends Specification {

    def bean
    List cliArguments

    def "two defined default values but only is set works"() {
        given:
        bean = new ObjectWithTwoDefaultValues()
        bean.def2 = 'value'

        expect:
        createCliArguments() == ['value']
    }

    def "two set default values throws exception"() {
        given:
        bean = new ObjectWithTwoDefaultValues()
        bean.def1 = 'aValue'
        bean.def2 = 'anotherValue'

        when:
        createCliArguments()
        then:
        thrown(CliArgsBuilderException)
    }

    def "test complex object"() {
        given:
        bean = new TestCliBean();
        bean.file = new File("C:\\tmp")
        bean.list = ["bla", "blub"]
        bean.map = [ key : 'value' ]
        bean.defaultarg = "default"

        when:
        createCliArguments()
        then:
        cliArgumentsContainAllOf("--file ${('C:\\tmp') as File}", '--list bla', '--list blub', '--map key=value', 'default')
        cliArguments[-1] == 'default'
    }

    void cliArgumentsContainAllOf(String... fragments) {
        def cliString = createCliArguments().join(" ")

        fragments.each {
            assert cliString =~ ~"(?<!\\S)${quote(it)}(?!\\S)"
        }
    }

    def "two identical arguments throws exception"() {
        given:
        bean = new ObjectWithTwoIdenticalArguments()
        bean.def1 = 'aValue'
        bean.def2 = 'anotherValue'

        when:
        createCliArguments()
        then:
        thrown(CliArgsBuilderException)
    }

    def createCliArguments() {
        cliArguments = new CliArgsBuilder(bean).createArguments()
    }


    @SuppressWarnings("UnusedDeclaration")
    public static class ObjectWithTwoDefaultValues {
        @CliArgsParameter(type=ArgumentType.DEFAULT)
        private String def1;

        @CliArgsParameter(type=ArgumentType.DEFAULT)
        private String def2;

        public void setDef1(String def1) {
            this.def1 = def1;
        }

        public void setDef2(String def2) {
            this.def2 = def2;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class ObjectWithTwoIdenticalArguments {
        @CliArgsParameter(argument="arg")
        private String def1;

        @CliArgsParameter(argument="arg")
        private String def2;

        public void setDef1(String def1) {
            this.def1 = def1;
        }

        public void setDef2(String def2) {
            this.def2 = def2;
        }
    }


}