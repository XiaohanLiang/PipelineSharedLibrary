#!/usr/bin/env groovy

def call(def env) {

    // mkdir + meta(Store some runtime meta info)
    //         ws(We use to clone,build,upload,etc)
    String entrance = env.WORKSPACE
    assert entrance.length() > 0 : "Cannot find WORKSPACE"
    println(entrance)
    def entranceExists = fileExists entrance
    println("Entrance")
    println(entranceExists)
    def upper = fileExists "/var/lib/jenkins/workspace"
    println("Upper")
    println(upper)
    if (!entranceExists) {
        echo "Entered if cluse"
        makeDir(entrance)
        println("EntranceInside")
        def entranceExists2 = fileExists entrance
        println(entranceExists2)

    }

    String meta = entrance + "/.JD_CODE_BUILD"
    println("meta")
    println(meta)

    makeDir(meta)
    def m = fileExists meta
    println("m")
    println(m)

    String ws = entrance + "/workspace"
    println("ws")
    println(ws)

    makeDir(ws)
    def w = fileExists meta
    println("w")
    println(w)

    String runtimeEnvFile = meta + "/buildRuntimeEnv"
    println("c")
    touchFile(runtimeEnvFile)
    println("d")

    /*
     * Todo -  
     *           Obviously compile-server need more than these two parameters 
     *           In order to replace BuildCMD completely, we need to trace all 
     *           parameters required in the following scripts. eg. package size
     */
    // Read task runtime variable
    File f = new File(runtimeEnvFile)
    f << readFile("/var/tmp/REGION_ID") << "\n"

    // Git Operations
    String setConfigCommand = "git config --local --unset credential.helper"
    String setCredentialCmd = "git config credential.helper \"store --file="+ entrance +"/.JD_CODE_BUILD/.git-credentials\""
    def setConfig = setConfigCommand.execute()
    def setCredential = setCredentialCmd.execute()
    
    return entrance + "/workspace"
}

void makeDir(String path){
    println("1")
    File f = new File(path)
    println("2")
    f.mkdir()
    println("3")
}

void touchFile(String filePath){
    def newFile = new File(filePath)
    newFile.createNewFile()
}

String readFile(String filePath) {
    File file = new File(filePath)
    return file.text
}