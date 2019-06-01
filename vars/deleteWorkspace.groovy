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

    println("1")
    assert path.length()>0 :"Empty path given"
    println("2")
//    def entrance = new File(path)
    println("3")
    def entrance = fileExists path
    println("4")
    if (!entrance) {
        return
    }

    println("Cleaning workspace..")

    def ws = new File(path + "/workspace")
    println("5")
    def meta = new File(path + "/.JD_CODE_BUILD")
    println("6")

    // Workspace will be deleted when required
    if ( depth == "ALL" && ws.exists() ) {
        ws.deleteDir()
    }
    println("7")

    // Meta info will deleted as long as it exists
    if ( meta.exists() ){
        meta.deleteDir()
    }
}
