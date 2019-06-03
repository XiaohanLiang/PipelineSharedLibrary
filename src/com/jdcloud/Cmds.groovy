#!/usr/bin/env groovy
package com.jdcloud

@Grab('org.yaml:snakeyaml:1.17')
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

    def Cmds(String path) {
        Yaml yaml = new Yaml()
        println(path)
        println(is)
        InputStream is = Cmds.class.getResourceAsStream(path)
        this.cmds = yaml.loadAs(is, Cmds.class)
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

    Cmd GenerateExportTask(){
        Cmd ExportTask = new Cmd()
        ExportTask.name = "Setting environment vaiables: " + name
        ExportTask.cmd = "export " + name + "=" + value
        return ExportTask
    }
}
