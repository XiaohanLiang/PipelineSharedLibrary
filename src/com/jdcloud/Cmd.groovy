package com.jdcloud

class Cmd {

    def name
    def cmd

    Cmd(def n,def c){
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