#!/usr/bin/env groovy
package com.jdcloud

@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

class ParseYaml{

    def Cmds c
    ParseYaml(String path){

        Yaml yaml = new Yaml()
        File file = new File(path)
        FileInputStream stream = new FileInputStream(file);
        this.c = yaml.loadAs(stream, Cmds.class)

    }
}