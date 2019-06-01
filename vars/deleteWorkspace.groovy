#!/usr/bin/env groovy
import java.io.File

def call(def path){
    assert path.length()>0 :"Empty path given"
    def ws = new File(path)
    println("Cleaning workspace..")
    ws.deleteDir()
}
