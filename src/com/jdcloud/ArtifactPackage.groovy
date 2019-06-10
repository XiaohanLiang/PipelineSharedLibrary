#!/usr/bin/env groovy
package com.jdcloud

import java.security.MessageDigest;
import java.io.*;

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
    String MetaSpace
    String ArtifactSpace


    ArtifactPackage(def ws,def upload,def bucketName,def bucketPath,def endpoint,def ak,def sk){

        this.UploadArtifact = upload
        this.CompilerOssBucket = bucketName
        this.CompilerOssPath = bucketPath
        this.CompilerOssEndpoint = endpoint
        this.AccessKey = ak
        this.SecretKey = sk
        this.MetaSpace = ws + "/meta"
        this.ArtifactSpace = ws + "/artifact"
    }

    def GetPackageNameWithPath(){

        def moduleName = this.CompileModuleName.toLowerCase().replaceAll("_","-")
        def branch = this.Branch
        if (branch.split("/").size() >=2 ) {
            branch = branch.split("/").get(1)
        }
        def commitId = this.Commit[0..8]
        return this.ArtifactSpace + moduleName + "-" + branch + "-" + System.currentTimeSeconds() + "tar.gz"
    }

    def Packaging(){

        def packageName = GetPackageNameWithPath()
        def packageCommand = "tar zcvf " + packageName + " " + this.OutputSpace

        this.script.echo "Packaging -> " + packageName

        def Stdout = new StringBuilder()
        def Stderr = new StringBuilder()
        def start = packageCommand.execute()
        start.consumeProcessOutput(Stdout, Stderr)
        start.waitForOrKill(3600 * 1000)

        this.script.echo ">      $Stdout"
        this.script.echo "------------"
    }

    def MD5Hash(){
        def packageName = GetPackageNameWithPath()
        File p = new File(packageName);
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        return getFileChecksum(md5Digest, p);
    }

    def getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    def GenerateProfilingScript() {

        File ws = new File(this.MetaSpace)
        File script = File.createTempFile("Jenkins-Profiling-", ".sh", ws)
        script.setExecutable(true)
        script.setWritable(true)
        script.deleteOnExit()

        def scriptPath = script.getAbsolutePath()
        PrintWriter pencil = new PrintWriter(scriptPath)

        // Prepare packaging command
        pencil.println(GetPackagingCommand())

        // MD5Sum Calculating md5sum  tf.tar.gz | awk '{print $1}'


        // AWS-Signing-Uploading

        // Java-SDK uploading
    }

    def Execute(){

        def packageName = GetPackageNameWithPath()
        this.script.echo  "We have packageName="
        this.script.echo  packageName
//        Packaging()

    }
}
