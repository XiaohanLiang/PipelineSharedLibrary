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