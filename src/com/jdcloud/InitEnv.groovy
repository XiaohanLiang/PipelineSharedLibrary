#!/usr/bin/env groovy
package com.jdcloud


class InitEnv {

    String JenkinsWorkSpace
    String UserWorkSpace
    String ArtifactSpace
    String MetaSpace
    String CacheSpace
    String RuntimeEnv
    Script script

    InitEnv(def j,def s){
        this.JenkinsWorkSpace = j
        this.UserWorkSpace = j + "/workspace/"
        this.ArtifactSpace = j + "/artifact/"
        this.MetaSpace = j + "/meta/"
        this.CacheSpace = j + "/cache/"
        this.RuntimeEnv = j + "/meta/buildRuntimeEnv"
        this.script = s
    }

    def CreatePath(){
        createPath(this.CacheSpace)
        createPath(this.UserWorkSpace)
        createPath(this.ArtifactSpace)
        createPath(this.MetaSpace)
    }
    def createPath(String exp){
        File f = new File(exp)
        f.mkdir()
    }
    def CreateFile(String exp){
        def newFile = new File(exp)
        newFile.createNewFile()
    }

    def RecordRegionInfo(){
        File f = new File(this.RuntimeEnv)
        f << ReadFile("/var/tmp/REGION_ID") << "\n"
    }

    def GitInit(){

        this.script.echo "Command Run"
        String setConfigCommand = "git config --local --unset credential.helper"
        String setCredentialCmd = "git config credential.helper 'store --file="+ this.MetaSpace +".git-credentials'"

        dir(this.UserWorkSpace){
            this.script.echo "Entering UserWorkSpace"
            def setConfig = setConfigCommand.execute()
            def setCredential = setCredentialCmd.execute()
        }

    }

    def Cleaning(String pattern) {
        // Pattern and cleaning
    }

    def ReadFile(String filePath) {
        File file = new File(filePath)
        return file.text
    }

    def Execute(){

        CreatePath()
        CreateFile(this.RuntimeEnv)
        GitInit()
        RecordRegionInfo()
        Cleaning()
    }
}
