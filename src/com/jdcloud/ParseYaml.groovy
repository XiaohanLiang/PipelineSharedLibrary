package com.jdcloud

@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.Yaml

class ParseYaml{

    def Cmds c
    ParseYaml(String path){

//        Yaml yaml = new Yaml()
//        File file = new File(path)
//        FileInputStream stream = new FileInputStream(file);
//        this.c = yaml.loadAs(stream, Cmds.class)
    }

    def SetUp(){
        String yamlStr = "key: hello yaml"
        Yaml yaml = new Yaml()
        Object ret = yaml.load(yamlStr)
        return ret
    }

    def SetUp2(){
        Yaml yaml = new Yaml()
//        Yaml yaml = new Yaml(new Constructor(Address.class))
//        File file = new File("/root/bb.yaml")
//        FileInputStream stream = new FileInputStream(file)
//        def c = yaml.loadAs(stream, Address.class)
        Address cc = new Address("123","123","123","123")
        def c = yaml.load(("/root/build.yaml" as File).text)
        return c
    }
}