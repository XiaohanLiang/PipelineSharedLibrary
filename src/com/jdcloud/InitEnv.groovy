#!/usr/bin/env groovy
package com.jdcloud
import java.io.File;

class InitEnv {

    String JenkinsWorkSpace
    String UserWorkSpace
    String ArtifactSpace
    String MetaSpace
    String CacheSpace
    String RuntimeEnv
    Script script

    InitEnv(def env,def s){
        this.JenkinsWorkSpace = env.JenkinsWorkSpace
        this.UserWorkSpace = env.UserWorkSpace
        this.ArtifactSpace = env.ArtifactSpace
        this.MetaSpace = env.MetaSpace
        this.CacheSpace = env.CacheSpace
        this.RuntimeEnv = env.RuntimeEnv
        this.script = s
        this.script.echo env.JenkinsWorkSpace
        this.script.echo env.UserWorkSpace
        this.script.echo env.ArtifactSpace
        this.script.echo env.MetaSpace
        this.script.echo env.CacheSpace
        this.script.echo env.RuntimeEnv
    }

//    InitEnv(def j,def u,def a,def m,def c,def r,def s){
//        this.JenkinsWorkSpace = j
//        this.UserWorkSpace = u
//        this.ArtifactSpace = a
//        this.MetaSpace = m
//        this.CacheSpace = c
//        this.RuntimeEnv = r
//        this.script = s
//    }

    def CreatePath(){
        createPath(this.JenkinsWorkSpace)
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

        this.script.dir(this.UserWorkSpace){
            this.script.echo "Entering UserWorkSpace"
            def setConfig = setConfigCommand.execute()
            def setCredential = setCredentialCmd.execute()
        }

    }

    def Cleaning(String pattern) {

        this.script.dir(this.UserWorkSpace){
            this.script.deleteDir()
        }

        this.script.dir(this.MetaSpace){
            this.script.deleteDir()
        }

    }

    def ReadFile(String filePath) {
        File file = new File(filePath)
        return file.text
    }

    def Execute(){

        Cleaning()
        CreatePath()
        CreateFile(this.RuntimeEnv)
        RecordRegionInfo()
    }
}
