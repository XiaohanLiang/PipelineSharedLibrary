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
    def e

    InitEnv(def env,def s){
        this.JenkinsWorkSpace = env.JenkinsWorkSpace
        this.UserWorkSpace = env.UserWorkSpace
        this.ArtifactSpace = env.ArtifactSpace
        this.MetaSpace = env.MetaSpace
        this.CacheSpace = env.CacheSpace
        this.RuntimeEnv = env.RuntimeEnv
        this.script = s
        this.e = env
    }

    def CreatePath(){
        createPath(this.JenkinsWorkSpace)
        createPath(this.CacheSpace)
        createPath(this.UserWorkSpace)
        createPath(this.ArtifactSpace)
        createPath(this.MetaSpace)
        CreateFile(this.RuntimeEnv)
    }
    def createPath(String exp){
//        File f = new File(exp)
//        f.mkdir()
        this.script.dir(exp){}
    }
    def CreateFile(String exp){
        def newFile = new File(exp)
        newFile.createNewFile()
    }

    def RecordRegionInfo(){
        File f = new File(this.RuntimeEnv)
        f << ReadFile("/var/tmp/REGION_ID")
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

    def CheckParameters(){

        checkParametersNonNil(this.e.JenkinsWorkSpace)
        checkParametersNonNil(this.e.SCM_URL)
        checkParametersNonNil(this.e.SCM_BRANCH)
        checkParametersNonNil(this.e.USE_JDCLOUD_YAML)
        checkParametersNonNil(this.e.UPLOAD_ARTIFACT)
        checkParametersNonNil(this.e.COMPILE_MODULE_NAME)
        checkParametersNonNil(this.e.BUILD_IMAGE)
        checkParametersNonNil(this.e.COMPILER_TYPE)

        if (this.e.USE_JDCLOUD_YAML == "0"){
            checkParametersNonNil(this.e.YAML)
        }

        if (this.e.UPLOAD_ARTIFACT == "1"){

            if (this.e.COMPILER_TYPE == "PACKAGE"){
                checkParametersNonNil(this.e.OSS_BUCKET_NAME)
                checkParametersNonNil(this.e.OSS_BUCKET_ENDPOINT)
                checkParametersNonNil(this.e.OSS_ACCESS_KEY)
                checkParametersNonNil(this.e.OSS_SECRET_KEY)
            }else{
                checkParametersNonNil(this.e.DOCKER_REPOSITORY)
                checkParametersNonNil(this.e.DOCKER_LOGIN_TOKEN)
                checkParametersNonNil(this.e.DOCKER_REGISTRY_URI)
            }
        }



        checkFileExists(this.e.JenkinsWorkSpace)
        checkFileExists(this.e.UserWorkSpace)
        checkFileExists(this.e.ArtifactSpace)
        checkFileExists(this.e.MetaSpace)
        checkFileExists(this.e.CacheSpace)
        checkFileExists(this.e.RuntimeEnv)

    }
    def checkParametersNonNil(String s){
        if (s.length()==0){
            this.script.error("Failed, parameter is supposed to be non-nil")
        }
    }
    def checkFileExists(String d){
        def dir = new File(d)
        if (!dir.exists()){
            this.script.error("File " + d + " not exists while supposed to")
        }
    }

    def Execute(){

        Cleaning()

        CreatePath()

        CheckParameters()

        RecordRegionInfo()
    }
}
