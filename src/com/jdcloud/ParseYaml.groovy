package com.jdcloud

@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

class ParseYaml{

    public String lines;
    public String city;
    public String state;
    public String postal;

    def SetUp(){
        String yamlStr = "key: hello yaml"
        Yaml yaml = new Yaml()
        Object ret = yaml.load(yamlStr)
        return ret
    }

    static ParseYaml fromYaml() {

//        Yaml yaml = new Yaml()
//        Yaml yaml = new Yaml(new Constructor(Address.class))
        Constructor c = new Constructor(ParseYaml)
        File file = new File("/root/bb.yaml")
        FileInputStream stream = new FileInputStream(file)
//        def c = yaml.loadAs(stream, Address.class)
//        Address cc = new Address("123","123","123","123")
//        def c = (Address) yaml.load(("/root/build.yaml" as File).text)
        new Yaml(c).load(stream)
    }
}

String exampleYaml = '''widgets:
                       |  - name: blah
                       |    age: 3000
                       |    silly: true
                       |  - name: blah meh
                       |    age: 13939
                       |    silly: false
                       |uuid: 1938484
                       |isActive: false'''.stripMargin()


@ToString(includeNames=true)
class Widget {
    String name
    Integer age
    boolean silly
}

@ToString(includeNames=true)
class MyConfig {
    List<Widget> widgets
    String uuid
    boolean isActive

    static MyConfig fromYaml(yaml) {
        Constructor c = new Constructor(MyConfig)
        TypeDescription t = new TypeDescription(MyConfig)
        t.putListPropertyType('widgts', Widget)
        c.addTypeDescription(t);

        new Yaml(c).load(yaml)
    }
}