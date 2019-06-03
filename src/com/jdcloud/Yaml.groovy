#!/usr/bin/env groovy
package com.jdcloud

class ParseYaml{

    def Cmds c
    ParseYaml(String path){

        Yaml yaml = new Yaml()
        File file = new File(path)
        FileInputStream stream = new FileInputStream(file);
        this.c = yaml.loadAs(stream, Cmds.class)

    }
}