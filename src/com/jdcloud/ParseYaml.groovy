package com.jdcloud
import com.jdcloud.Address
import com.jdcloud.Cmds
import org.apache.ivy.ant.AddPathTask
import org.junit.Assert
@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import javax.xml.ws.soap.Addressing

/*
  Discard in future
 *

*/
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
        Yaml yaml = new Yaml(new Constructor(Address.class))
//        File file = new File("/root/bb.yaml")
//        FileInputStream stream = new FileInputStream(file)
//        def c = yaml.loadAs(stream, Address.class)
        Address cc = new Address("123","123","123","123")
        def c = yaml.load(("/root/bb.yaml" as File).text)
        return c
    }
}