#!/usr/bin/env groovy
package com.jdcloud

import sun.font.Script

class ArtifactPackage {

    String UploadArtifact

    String ArtifactPath
    String CompilerOssBucket
    String CompilerOssPath
    String CompilerOssEndpoint
    String AccessKey
    String SecretKey
    String WorkSpace
    String CompileModuleName
    String Branch
    String Commit
    Script script


    String MD5Sum
    String OutputSpace


    ArtifactPackage(def ws,def upload,def artifactPath,def bucketName,def bucketPath,def endpoint,def ak,def sk){

        this.UploadArtifact = upload
        this.ArtifactPath = artifactPath
        this.CompilerOssBucket = bucketName
        this.CompilerOssPath = bucketPath
        this.CompilerOssEndpoint = endpoint
        this.AccessKey = ak
        this.SecretKey = sk
        this.WorkSpace = ws
    }

    def GetPackageName(){

        def moduleName = this.CompileModuleName.toLowerCase().replaceAll("_","-")
        def branch = this.Branch
        if (branch.split("/").size() >=2 ) {
            branch = branch.split("/").get(1)
        }
        def commitId = this.Commit[0..8]

        return this.WorkSpace + moduleName + "-" + branch + "-" + System.currentTimeSeconds() + "tar.gz"
    }

    def GetPackageCommand(def PackageName){
        def PackagingCommand = "tar zcvf " + PackageName + "-C "
        return PackagingCommand
    }

    def GenerateProfilingScript() {

        File ws = new File(this.WorkSpace)
        File script = File.createTempFile("Jenkins-Profiling-", ".sh", ws)
        script.setExecutable(true)
        script.setWritable(true)
        script.deleteOnExit()

        def scriptPath = script.getAbsolutePath()
        PrintWriter pencil = new PrintWriter(scriptPath)

        // Finding package + outputSpace
        def packageName = GetPackageName()
        def packageCommand = "tar zcvf " + packageNamePath + " -C " + outputSpace + " " + fileListStr
        pencil.println(packageCommand)

        // MD5Sum Calculating

        // AWS-Signing-Uploading

        // Java-SDK uploading
    }
}
