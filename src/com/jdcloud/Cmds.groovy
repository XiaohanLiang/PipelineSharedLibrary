#!/usr/bin/env groovy
package com.jdcloud
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

class Cmds {

    def List<Cmd> cmds
    def List<Env> envs
    def String out_dir

    Cmds(String path) {

        Yaml yaml = new Yaml()
        def settingMap = yaml.load((path as File).text)
        assertNotNull(settingMap)

        for( c in settingMap.cmds ){
            this.cmds = this.cmds + Cmd(c.name,c.command)
        }
        for ( e in settingMap.envs ) {
            this.envs = this.envs + Env(e.name,e.value)
        }
    }

    List<Env> GenerateEnvMap(ArrayList map){
        List<Env> EnvMap = []
        map.each{ k,v ->
            EnvMap = EnvMap + Env(k,v)
        }
        return EnvMap
    }

    List<Cmd> GenerateCmdMap(ArrayList map){
        List<Cmd> CmdMap = []
        map.each{ k,v ->
            CmdMap = CmdMap + Cmd(k,v)
        }
        return CmdMap
    }

    def Execute(){
        for (Cmd cmd : this.cmds ) {
            cmd.Execute()
        }
    }

    def GetCmds(){
        return this.cmds
    }

    def GetOutDir(){
        return this.out_dir
    }

    def PreprareEnvs(){
        for(Env e:this.envs) {
            this.cmds = e.GenerateExportTask() + this.cmds
        }
    }
}

class Cmd {

    def name
    def cmd

    Cmd(String n,c){
        this.name = n
        this.cmd = c
    }

    def Execute(){

        assert this.name.length() > 0 : "Invalid name given,expected not null"
        assert this.cmd.length() > 0 : "Invalid command given,expected not null "

        def Stdout = new StringBuilder()
        def Stderr = new StringBuilder()
        def start = this.cmd.execute()

        start.consumeProcessOutput(Stdout, Stderr)
        start.waitForOrKill(3600)

        println "Executing command: " + this.name
        println "\$      " + this.Command
        println ">      $Stdout"
        println "------------"

    }

}

class Env {

    def name
    def value

    Env(String n,v){
        this.name = n
        this.value = v
    }

    Cmd GenerateExportTask(){
        Cmd ExportTask = new Cmd()
        ExportTask.name = "Setting environment vaiables: " + name
        ExportTask.cmd = "export " + name + "=" + value
        return ExportTask
    }
}
