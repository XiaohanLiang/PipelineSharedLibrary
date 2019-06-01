#!/usr/bin/env groovy
import java.io.File

/**
 * Todo -
 *        Require more test on whether folder exists / available
 *
 */
def call(def path,def depth="ALL"){

    println("We have path")
    println(path)

    assert path.length()>0 :"Empty path given"
    println("11111")
    def entrance = new File(path)
    println("122222")
    if (!entrance.exists()) {
        return
    }

    println("Cleaning workspace..")

    if(depth == "ALL") {
        def ws = new File(path + "/workspace")
        ws.deleteDir()
    }

    def meta = new File(path + "/.JD_CODE_BUILD")
    meta.deleteDir()
}
