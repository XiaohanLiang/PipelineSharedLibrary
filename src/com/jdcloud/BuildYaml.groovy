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

    Map commands
    Map environments
    String output

    BuildYaml(String path) {

        Yaml yaml = new Yaml()
        def settingMap = yaml.load((path as File).text)
        assertNotNull(settingMap)

        commands = [:]
        environments = [:]
        
        for( c in settingMap.cmds ){
            this.commands[c.name] = c.command
        }
        for ( e in settingMap.envs ) {
            this.environments[e.name] = e.value
        }
        this.output = settingMap.out_dir == null ? "output" : settingMap.out_dir
    }

    def ExecuteCommand(){

        this.commands.each { name,command ->

            if (name.length()==0 || command.length()==0) {
                return
            }

            def Stdout = new StringBuilder()
            def Stderr = new StringBuilder()
            def start = command.execute()

            start.consumeProcessOutput(Stdout, Stderr)
            start.waitForOrKill(3600)

            println "Executing command: " + name
            println "\$      " + command
            println ">      $Stdout"
            println "------------"

        }
    }

    def say(){
        echo this.output
        println this.output
    }

}