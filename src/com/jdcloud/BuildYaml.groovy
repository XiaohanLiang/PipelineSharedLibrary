#!/usr/bin/env groovy
package com.jdcloud
import com.jdcloud.*

import static org.junit.Assert.*

@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.Yaml

/**
 * Todo -
 *          Complete it with os.Execute and define
 *          How to execute a command
 *          Eventually define how an array of tasks
 *          to do. Now I think probably we need a method
 *          To first load some commands during preparation stage
 *          And Execute it later on...
 *
 */

class BuildYaml {

    def List<Map> commands
    def List<Map> environments
    def String output

    BuildYaml(String path) {

        Yaml yaml = new Yaml()
        def settingMap = yaml.load((path as File).text)
        assertNotNull(settingMap)

        this.commands = []
        this.environments = []
        
        for( c in settingMap.cmds ){
            def m = [:]
            m[c.name] = c.command
            this.commands = this.commands + m
        }
        for ( e in settingMap.envs ) {
            def m = [:]
            m[e.name] = e.value
            this.environments = this.environments + m
        }
    }

//    def Execute(){
//        for (Cmd cmd : this.cmds ) {
//            cmd.Execute()
//        }
//    }
//
//    def GetCmds(){
//        return this.cmds
//    }
//
//    def GetOutDir(){
//        return this.out_dir
//    }
//
//    def PreprareEnvs(){
//        for(Env e:this.envs) {
//            this.cmds = e.GenerateExportTask() + this.cmds
//        }
//    }
}