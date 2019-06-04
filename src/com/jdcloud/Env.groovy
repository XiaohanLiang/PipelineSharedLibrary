package com.jdcloud

class Env {

    def name
    def value

    Env(def n,def v){
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

