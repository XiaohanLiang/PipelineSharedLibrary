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
        this.script.echo ("Preparing necessary tools...[FINISHED]")

    }

    def createPath(String exp){
        this.script.sh(returnStdout: false,script:"mkdir -p ${exp}")
    }

    def RecordRegionInfo(){
        def regionId = this.script.readFile('/var/tmp/REGION_ID')
        this.script.echo "Here the regionId=${regionId}"
        this.script.writeFile file:this.RuntimeEnv, text:regionId
    }

    def Cleaning(String pattern) {

        this.script.dir(this.UserWorkSpace){
            this.script.deleteDir()
        }

        this.script.dir(this.MetaSpace){
            this.script.deleteDir()
        }

        this.script.echo ("Cleaning workspace...[FINISHED]")
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
    }


    def checkParametersNonNil(String s){
        if (s.length()==0){
            this.script.error("Failed, parameter is supposed to be non-nil")
        }
    }

    def Execute(){

        Cleaning()

        CreatePath()

        CheckParameters()

        RecordRegionInfo()
    }
}
