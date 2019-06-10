#!/usr/bin/env groovy
package com.jdcloud

import java.security.MessageDigest;

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
    String PackageNameWithPath


    ArtifactPackage(def s,def ws,def outputSpace,def upload,def moduleName,def branch,def commit,def bucketName,def bucketPath,def endpoint,def ak,def sk){

        this.script = s
        this.UploadArtifact = upload
        this.CompileModuleName = moduleName
        this.OutputSpace = outputSpace
        this.Branch = branch
        this.Commit = commit
        this.CompilerOssBucket = bucketName
        this.CompilerOssPath = bucketPath
        this.CompilerOssEndpoint = endpoint
        this.AccessKey = ak
        this.SecretKey = sk
        this.MetaSpace = ws + "/meta/"
        this.ArtifactSpace = ws + "/artifact/"
    }

    def SetPackageName(){
        def moduleName = this.CompileModuleName.toLowerCase().replaceAll("_","-")
        def branch = this.Branch
        if (branch.split("/").size() >=2 ) {
            branch = branch.split("/").get(1)
        }
        def commitId = this.Commit[0..8]
        this.PackageNameWithPath = this.ArtifactSpace + moduleName + "-" + branch + "-" + System.currentTimeSeconds() + ".tar.gz"
    }

    def Packaging(){

        SetPackageName()
        def packageName = this.PackageNameWithPath
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

        def packageName = this.PackageNameWithPath
        File p = new File(packageName);
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        def md5 = getFileChecksum(md5Digest, p)

        this.script.echo  "Package MD5Sum = " + md5
        return md5
    }

    def getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = fis.read(byteArray)

        //Read file data and update in message digest
        for ( ; bytesCount != -1 ;bytesCount = fis.read(byteArray)){
            digest.update(byteArray, 0, bytesCount);
        }

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

    def RecordRuntimeEnv(String pattern) {
        // Assume buildRuntimeEnv exists
        this.script.echo "Target -> " + this.MetaSpace + "buildRuntimeEnv"
        this.script.echo "Pattern -> " + pattern
        File f = new File(this.MetaSpace + "buildRuntimeEnv")
        f << pattern << "\n"
    }

    def Execute(){

        Packaging()

        def hash = MD5Hash()
        RecordRuntimeEnv("COMPILER_PACKAGE_MD5SUM="+hash)

        // AWS-Signing-Uploading

        // Java-SDK uploading
    }
}
