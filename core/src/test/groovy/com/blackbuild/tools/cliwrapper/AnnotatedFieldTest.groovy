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
        '@CliBuilderParameter String string'                           | 'value'          || ['--string', 'value']
        '@CliBuilderParameter(type=DEFAULT) String defaultarg'         | 'value'          || ['value']
        '@CliBuilderParameter(argument="-", type=DEFAULT) String dash' | 'value'          || ['-', 'value']
        '@CliBuilderParameter(argument="override") String renamed'     | 'bla'            || ['--override', 'bla']
        '@CliBuilderParameter String[] array'                          | [ 'bla', 'blub'] || ["--array", "bla", "--array", "blub"]
        '@CliBuilderParameter List<String> list'                       | [ 'bla', 'blub'] || ["--list", "bla", "--list", "blub"]
        '@CliBuilderParameter(delimiter=",") List<String> commaList'   | [ 'bla', 'blub'] || ["--commaList", "bla,blub"]
        '@CliBuilderParameter(delimiter=" ") List<String> spaceList'   | [ 'bla', 'blub'] || ["--spaceList", "bla", "blub"]

        '@CliBuilderParameter File file'                               | new File("/tmp/test/")      || [ "--file", new File("/tmp/test/").toString() ]
        '@CliBuilderParameter Map map'                                 | [ bla : 'blu', bli : 'blo'] || ["--map", "bla=blu", "--map", "bli=blo"]
        '@CliBuilderParameter(assignment=" ") Map spaceAssignedMap'    | [ bla : 'blu', bli : 'blo'] || ["--spaceAssignedMap", "bla" , "blu", "--spaceAssignedMap", "bli", "blo"]
    }

    def "null field results in an empty cli fragment"() {
        given:
        aFieldWithDefinitionAndValue( '@CliBuilderParameter String string', null)

        expect:
        !field.cliFragment
    }

    def aFieldWithDefinitionAndValue(String definition, value) {
        bean = loader.parseClass(
                """import com.blackbuild.tools.cliwrapper.CliBuilderParameter
                import static com.blackbuild.tools.cliwrapper.ArgumentType.*
                public class TestBean {
                    $definition
                }""").newInstance()
        def fieldName = definition.split(/\s+/)[-1]
        bean."$fieldName" = value
        field = new CliField(bean, bean.class.getDeclaredField(fieldName))

    }
}