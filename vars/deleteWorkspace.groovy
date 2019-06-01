#!/usr/bin/env groovy
import java.io.File

/**
 * Todo -
 *         Users may want to keep their workspace, but deleting their
 *         runtime env is a must, so we have to provide two options:
 *                  Remove Ws -> Delete entire path
 *                  Keep Ws -> Delete .JD_CODE_BUILD only
 *
 */
def call(def path){
    assert path.length()>0 :"Empty path given"
    def ws = new File(path)
    println("Cleaning workspace..")
    ws.deleteDir()
}
