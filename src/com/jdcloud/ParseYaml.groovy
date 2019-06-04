package com.jdcloud

//@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

class ParseYaml{

    public String lines;
    public String city;
    public String state;
    public String postal;

    ParseYaml(){}

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