#!/usr/bin/env groovy

def call(def env) {

    // mkdir + meta(Store some runtime meta info)
    //         ws(We use to clone,build,upload,etc)
    Map envMap =  System.getenv()
    println(env)
    println(envMap["PATH"])
    sh("pwd")
    sh("ls")
    sh("env")
    String generalWorkingDirectory = envMap["WORKSPACE"]
//    assert generalWorkingDirectory.length() > 0 : "C  annot find WORKSPACE"
    String meta = generalWorkingDirectory + "/.JD_CODE_BUILD"
    makeDir(meta)
    String ws = generalWorkingDirectory + "/workspace"
    makeDir(ws)
    String runtimeEnvFile = meta + "/buildRuntimeEnv"
    touchFile(runtimeEnvFile)

    // Read task runtime variable2
    File f = new File(runtimeEnvFile)
//    Map envMap =  System.getenv()
    envMap.each {
        if (it.key.matches("COMPILER_(.*)") || it.key.matches("GIT_(.*)") || it.key == "WORKSPACE") {
            f << it.key << "=" << it.value << "\n"
        }
    }
    f << readFile("/var/tmp/REGION_ID") << "\n"

    // Git Operations
    String setConfigCommand = "git config --local --unset credential.helper"
    String setCredentialCmd = "git config credential.helper \"store --file="+ generalWorkingDirectory +"/.JD_CODE_BUILD/.git-credentials\""
    def setConfig = setConfigCommand.execute()
    def setCredential = setCredentialCmd.execute()

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