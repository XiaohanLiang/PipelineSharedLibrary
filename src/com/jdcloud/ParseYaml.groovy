#!/usr/bin/env groovy
package com.jdcloud

@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

public class Address {
    private String lines;
    private String city;
    private String state;
    private Integer postal;
}

class ParseYaml{

    def Cmds c
    ParseYaml(String path){

//        Yaml yaml = new Yaml()
//        File file = new File(path)
//        FileInputStream stream = new FileInputStream(file);
//        this.c = yaml.loadAs(stream, Cmds.class)
    }

    def SetUp(){
        String yamlStr = "key: hello yaml";
        Yaml yaml = new Yaml();
        Object ret = yaml.load(yamlStr);
        return ret
    }

    def SetUp2(){
        Yaml yaml = new Yaml()
        File file = new File("/root/build.yaml")
        FileInputStream stream = new FileInputStream(file);
        Address c = yaml.loadAs(stream, Address.class)
        return c
    }
}