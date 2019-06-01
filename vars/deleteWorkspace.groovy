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
    def entranceExists = fileExists path
    println(entranceExists)
    println(entranceExists == false)
    println(entranceExists == "false")
    println("4")
//    if (entranceExists == false) {
//        return 1
//    }

    println("Cleaning workspace..")

//    def ws = new File(path + "/workspace")
    def ws = path + "/workspace"
    def meta = path + "/.JD_CODE_BUILD"
    def wsExists = fileExists ws
    def metaExists = fileExists meta

    // Workspace will be deleted when required
//    if ( depth == "ALL" && wsExists ) {
//        ws.deleteDir()
//    }
    println("7")

    // Meta info will deleted as long as it exists
//    if (metaExists){
//        meta.deleteDir()
//    }
}
