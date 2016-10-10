package com.blackbuild.tools.cliwrapper

import spock.lang.Specification
import spock.lang.Unroll

class AnnotatedFieldTest extends Specification {

    def bean
    CliField field
    GroovyClassLoader loader = new GroovyClassLoader()

    @Unroll
    def "assignment to '#definition'"() {
        given:
        aFieldWithDefinitionAndValue(definition, value)

        expect:
        field.cliFragment == result

        where:
        definition                                                     | value            || result
        '@CliArgsParameter String string'                           | 'value'          || ['--string', 'value']
        '@CliArgsParameter(type=DEFAULT) String defaultarg'         | 'value'          || ['value']
        '@CliArgsParameter(argument="-", type=DEFAULT) String dash' | 'value'          || ['-', 'value']
        '@CliArgsParameter(argument="override") String renamed'     | 'bla'            || ['--override', 'bla']
        '@CliArgsParameter String[] array'                          | [ 'bla', 'blub'] || ["--array", "bla", "--array", "blub"]
        '@CliArgsParameter List<String> list'                       | [ 'bla', 'blub'] || ["--list", "bla", "--list", "blub"]
        '@CliArgsParameter(delimiter=",") List<String> commaList'   | [ 'bla', 'blub'] || ["--commaList", "bla,blub"]
        '@CliArgsParameter(delimiter=" ") List<String> spaceList'   | [ 'bla', 'blub'] || ["--spaceList", "bla", "blub"]

        '@CliArgsParameter File file'                               | new File("/tmp/test/")      || [ "--file", new File("/tmp/test/").toString() ]
        '@CliArgsParameter Map map'                                 | [ bla : 'blu', bli : 'blo'] || ["--map", "bla=blu", "--map", "bli=blo"]
        '@CliArgsParameter(assignment=" ") Map spaceAssignedMap'    | [ bla : 'blu', bli : 'blo'] || ["--spaceAssignedMap", "bla" , "blu", "--spaceAssignedMap", "bli", "blo"]
    }

    def "null field results in an empty cli fragment"() {
        given:
        aFieldWithDefinitionAndValue( '@CliArgsParameter String string', null)

        expect:
        !field.cliFragment
    }

    def aFieldWithDefinitionAndValue(String definition, value) {
        bean = loader.parseClass(
                """import com.blackbuild.tools.cliwrapper.CliArgsParameter
                import static com.blackbuild.tools.cliwrapper.ArgumentType.*
                public class TestBean {
                    $definition
                }""").newInstance()
        def fieldName = definition.split(/\s+/)[-1]
        bean."$fieldName" = value
        field = new CliField(bean, bean.class.getDeclaredField(fieldName))

    }
}