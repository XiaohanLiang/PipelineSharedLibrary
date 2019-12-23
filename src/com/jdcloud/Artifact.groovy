#!/usr/bin/env groovy
package com.jdcloud

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Artifact {

    String BuildTag

    String AccessKey
    String SecretKey
    String CompileModuleName
    String Branch
    String Commit
    Script script
    String OutputSpace
    String MetaSpace
    String ArtifactSpace
    String UserWorkSpace
    String PackageNameWithPath
    String RawPackageName

    String CompilerType
    String UploadArtifact

    String CompilerOssBucket
    String CompilerOssPath
    String CompilerOssEndpoint
    String CompilerPackageVersion

    def DockerLoginToken
    def DockerRegistryUri
    def DockerRepository


    Artifact (def env,def s) {

        this.script = s

        try {
            this.BuildTag = env.DOCKER_IMAGE_TAG
            if(this.BuildTag == ""){
                this.BuildTag = env.BUILD_TAG
            }
        }catch(MissingPropertyException){
            this.BuildTag = env.BUILD_TAG
        }

        this.UploadArtifact = env.UPLOAD_ARTIFACT
        this.CompileModuleName = env.COMPILE_MODULE_NAME
        this.OutputSpace = env.OutputSpace
        this.Branch = env.SCM_BRANCH
        this.Commit = env.SCM_COMMIT_ID
        this.CompilerOssBucket = env.OSS_BUCKET_NAME
        this.CompilerOssPath = env.OSS_BUCKET_PATH == "" ? env.OSS_BUCKET_PATH : env.OSS_BUCKET_PATH+"/"
        this.CompilerOssEndpoint = env.OSS_BUCKET_ENDPOINT
        this.AccessKey = env.OSS_ACCESS_KEY
        this.SecretKey = env.OSS_SECRET_KEY
        this.MetaSpace = env.MetaSpace
        this.ArtifactSpace = env.ArtifactSpace
        this.CompilerType = env.COMPILER_TYPE
        this.DockerRegistryUri = env.DOCKER_REGISTRY_URI
        this.DockerRepository = env.DOCKER_REPOSITORY
        this.DockerLoginToken = env.DOCKER_LOGIN_TOKEN
        this.UserWorkSpace = env.UserWorkSpace
    }

    def SetPackageName(){

        def moduleName = this.CompileModuleName.toLowerCase().replaceAll("_","-")
        def branch = branch.split("/").size() >=2 ? branch.split("/").get(1) : this.Branch
        def commitId = this.Commit.length() > 9 ? this.Commit[0..8] : this.Commit
        def timeTag = System.currentTimeSeconds()

        this.RawPackageName = moduleName + "-" + branch + "-" + timeTag + ".tar.gz"
        this.PackageNameWithPath = this.ArtifactSpace + moduleName + "-" + branch + "-" + timeTag + ".tar.gz"
        this.CompilerPackageVersion = branch + "-" + timeTag
    }

    def Packaging(){

        SetPackageName()
        def packageName = this.PackageNameWithPath
        def packageCommand = "tar zcvf ${packageName} ."
        this.script.echo "Packaging : ${this.OutputSpace} -> ${this.RawPackageName}"

        this.script.dir(this.UserWorkSpace) {
            this.script.dir(this.OutputSpace) {
                def ret = this.script.sh(returnStatus: true, script: "${packageCommand}")
                if (ret != 0) {
                    this.script.error("Failed in packaging, exiting..")
                }
            }
        }
    }

    def MD5Hash(){

        def packageName = this.PackageNameWithPath
        def md5 = this.script.sh(returnStdout: true,script:"#!/bin/sh -e\n  cat ${packageName} | md5sum | awk '{print \$1}'")
        return md5
    }

    def RecordRuntimeEnv(String pattern) {
        def content = this.script.readFile "${this.MetaSpace}buildRuntimeEnv"
        this.script.writeFile file: "${this.MetaSpace}buildRuntimeEnv" , text: "${content}\n${pattern}"
    }

    def CheckParameters(){

        assert this.CompileModuleName.length()>0
        assert this.OutputSpace.length()>0
        assert this.Branch.length()>0
        //assert this.Commit.length()>0

        assert this.UploadArtifact == "0" || this.UploadArtifact == "1"

        assert this.CompilerType == "IMAGE" || this.CompilerType == "PACKAGE"
        if(this.CompilerType == "IMAGE"){
           checkImageParameters()
        }
        if(this.CompilerType == "PACKAGE"){
           checkPackageParameters()
        }
    }

    def checkPackageParameters(){

        assert this.CompilerOssBucket.length()>0
        assert this.CompilerOssEndpoint.length()>0
        assert this.AccessKey.length()>0
        assert this.SecretKey.length()>0
    }

    def checkImageParameters(){

        assert this.DockerLoginToken.length()>0
        assert this.DockerRegistryUri.length()>0
        assert this.DockerRepository.length()>0
    }

    def UploadPackage(){

        this.script.dir(this.ArtifactSpace) {

            def shellString = this.script.libraryResource("jss")
            def shellFile = this.MetaSpace + "jss.sh"
            this.script.writeFile file: shellFile, text: shellString
            this.script.sh("#!/bin/sh -e\n chmod +x ${shellFile}")

            File art = new File(this.PackageNameWithPath)
            String fileName = art.getName()

            String args = sprintf(" -n %s -f %s -k %s -s %s -e %s -b %s", this.CompilerOssPath+fileName, fileName,this.AccessKey,
                    this.SecretKey, this.CompilerOssEndpoint, this.CompilerOssBucket)

            this.script.echo "Start uploading..."
            def ret = this.script.sh(returnStdout: true,script:"../meta/jss.sh" + args)
            if(ret!=""){
                this.script.error("Failed in uploading, Exit.")
            }
            this.script.echo "Uploading finished :)"
            return this.CompilerOssEndpoint + "/" + this.CompilerOssBucket + "/" + this.CompilerOssPath + fileName
        }

    }

    def PrepareImage(){

        if (isContainChinese(this.BuildTag)){
            this.BuildTag = "jenkins-" + System.currentTimeSeconds()
        }

        //Generate docker related Commands, we do this since docker login/rmi is not yet supported
        def login = sprintf("docker login -u jdcloud -p %s %s",this.DockerLoginToken,this.DockerRegistryUri)
        def buildCommand = sprintf("docker build -t %s:%s .",this.DockerRegistryUri,this.BuildTag)
        def pushCommand = sprintf("docker push %s:%s ",this.DockerRegistryUri,this.BuildTag)
        def rmiCommand = sprintf("docker rmi %s:%s ",this.DockerRegistryUri,this.BuildTag)

        // Start executing them
        this.script.dir(this.UserWorkSpace){
            this.script.sh login
            this.script.sh buildCommand
            this.script.sh pushCommand
            this.script.sh rmiCommand
        }

    }

    Boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    def Execute(){


        if(this.UploadArtifact == "1"){

            CheckParameters()

            if(this.CompilerType == "PACKAGE"){
            
                Packaging()
                def hash = MD5Hash()
                def url = UploadPackage()

                RecordRuntimeEnv("COMPILER_PACKAGE_MD5SUM="+hash)
                RecordRuntimeEnv("COMPILER_PACKAGE_URL="+url)
                RecordRuntimeEnv("UPLOAD_ARTIFACT=1")
                RecordRuntimeEnv("COMPILER_PACKAGE_VERSION="+this.CompilerPackageVersion)

            }

            if(this.CompilerType == "IMAGE"){

                PrepareImage()
                RecordRuntimeEnv("UPLOAD_ARTIFACT=1")
                RecordRuntimeEnv("COMPILER_PACKAGE_URL="+this.DockerRegistryUri+":"+this.BuildTag)
                RecordRuntimeEnv("COMPILER_PACKAGE_VERSION="+this.BuildTag)

            }
        }else{

            this.script.echo "[Profiling Artifact] step has been skipped :)"
            RecordRuntimeEnv("UPLOAD_ARTIFACT=0")

        }

        this.script.archiveArtifacts  artifacts: "meta/buildRuntimeEnv"

    }
}
