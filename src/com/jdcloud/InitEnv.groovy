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
    def Env

    InitEnv(def env,def s){

        CheckParameters(env)

        this.JenkinsWorkSpace = env.JenkinsWorkSpace
        this.UserWorkSpace = env.UserWorkSpace
        this.ArtifactSpace = env.ArtifactSpace
        this.MetaSpace = env.MetaSpace
        this.CacheSpace = env.CacheSpace
        this.RuntimeEnv = env.RuntimeEnv
        this.script = s
    }

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

    def CheckParameters(def env){

        assert env.JenkinsWorkSpace.length() > 0

        assert env.ScmUrl.length() > 0
        assert env.ScmBranch.length() > 0
        assert env.CommitID.length() > 0
        assert env.ScmCredential.length() > 0

        assert env.Yaml.length() > 0
        assert env.BuildImage.length() > 0

        assert env.UploadArtifact.length() > 0
        assert env.CompileModuleName.length() > 0
        assert env.OutputSpace.length() > 0
        assert env.OssBucketName.length() > 0
        assert env.OssBucketpath.length() > 0
        assert env.OssBucketEndpoint.length() > 0
        assert env.OssAccessKey.length() > 0
        assert env.OssSecretKey.length() > 0
    }

    def Execute(){

        CheckParameters()
        
        Cleaning()
        CreatePath()
        CreateFile(this.RuntimeEnv)
        RecordRegionInfo()
    }
}
