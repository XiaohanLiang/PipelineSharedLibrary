#!/usr/bin/env groovy
import java.io.File

def call() {

    // mkdir + meta(Store some runtime meta info)
    //         ws(We use to clone,build,upload,etc)
    echo "Entered"
    String generalWorkingDirectory = System.getenv("WORKSPACE")
    echo "Entered-1"
    sh("pwd")
    assert length(generalWorkingDirectory) > 0 : "Cannot find WORKSPACE"
    String meta = generalWorkingDirectory + "/.JD_CODE_BUILD"
    makeDir(meta)
    echo "Entered-2"
    String ws = generalWorkingDirectory + "/workspace"
    makeDir(ws)
    echo "Entered-3"
    String runtimeEnvFile = meta + "/buildRuntimeEnv"
    touchFile(runtimeEnvFile)

    // Read task runtime variable
    File f = new File(runtimeEnvFile)
    Map envMap =  System.getenv()
    envMap.each {
        String name = it.key
        if (name.matches("COMPILER_(.*)") || name.matches("GIT_(.*)") || name == "WORKSPACE") {
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