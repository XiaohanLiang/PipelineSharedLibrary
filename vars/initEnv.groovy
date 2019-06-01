#!/usr/bin/env groovy

def call(def env) {

    // mkdir + meta(Store some runtime meta info)
    //         ws(We use to clone,build,upload,etc)
    String entrance = env.WORKSPACE
    assert entrance.length() > 0 : "Cannot find WORKSPACE"
    String meta = entrance + "/.JD_CODE_BUILD"
    makeDir(meta)
    String ws = entrance + "/workspace"
    makeDir(ws)
    String runtimeEnvFile = meta + "/buildRuntimeEnv"
    touchFile(runtimeEnvFile)

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
    File f = new File(path)
    f.mkdir()
}

void touchFile(String filePath){
    def newFile = new File(filePath)
    newFile.createNewFile()
}

String readFile(String filePath) {
    File file = new File(filePath)
    return file.text
}