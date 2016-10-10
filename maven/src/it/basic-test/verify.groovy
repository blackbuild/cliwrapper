import static java.util.regex.Pattern.quote

File parameterFile = new File(basedir, 'user/target/parameters.txt')

def actualParameters = parameterFile.text

['--filter myfilter', '--include bla,blub', '--properties one=value', '--properties two=another', parameterFile.parent].each {
    assert actualParameters =~ ~"(?<!\\S)${quote(it)}(?!\\S)"
}

return true