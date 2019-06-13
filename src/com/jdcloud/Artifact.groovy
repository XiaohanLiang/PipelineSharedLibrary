#!/usr/bin/env groovy
package com.jdcloud

import java.security.MessageDigest;

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
    String JenkinsWorkSpace
    String PackageNameWithPath

    String CompilerType
    String UploadArtifact

    String CompilerOssBucket
    String CompilerOssPath
    String CompilerOssEndpoint

    def DockerLoginToken
    def DockerRegistry
    def DockerRepository


    Artifact (def env,def s) {

        this.script = s
        this.BuildTag = env.BUILD_TAG

        this.UploadArtifact = env.UploadArtifact
        this.CompileModuleName = env.CompileModuleName
        this.OutputSpace = env.OutputSpace
        this.Branch = env.ScmBranch
        this.Commit = env.CommitID
        this.CompilerOssBucket = env.OssBucketName
        this.CompilerOssPath = env.OssBucketpath
        this.CompilerOssEndpoint = env.OssBucketEndpoint
        this.AccessKey = env.OssAccessKey
        this.SecretKey = env.OssSecretKey
        this.MetaSpace = env.MetaSpace
        this.ArtifactSpace = env.ArtifactSpace
        this.CompilerType = env.CompilerType
        this.DockerRegistry = env.DockerRegistry
        this.DockerRepository = env.DockerRepository
        this.DockerLoginToken = env.DockerLoginToken
        this.JenkinsWorkSpace = env.JenkinsWorkSpace
    }

    def SetPackageName(){

        def moduleName = this.CompileModuleName.toLowerCase().replaceAll("_","-")
        def branch = branch.split("/").size() >=2 ? branch.split("/").get(1) : this.Branch
        def commitId = this.Commit.length() > 9 ? this.Commit[0..8] : this.Commit

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

    def CheckParameters(){

        assert this.CompileModuleName.length()>0
        assert this.OutputSpace.length()>0
        assert this.Branch.length()>0
        assert this.Commit.length()>0

        assert this.UploadArtifact == "0" || this.UploadArtifact == "1"

        assert this.CompilerType == "Image" || this.CompilerType == "Package"
        if(this.CompilerType == "Image"){
           checkImageParameters() 
        }
	if(this.CompilerType == "Package"){
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
        assert this.DockerRegistry.length()>0
        assert this.DockerRepository.length()>0
    }

    def UploadPackage(){

        this.script.dir(this.ArtifactSpace) {

            def shellString = this.script.libraryResource("jss")
            def shellFile = this.MetaSpace + "jss.sh"
            this.script.writeFile file: shellFile, text: shellString

            File shell = new File(this.MetaSpace + "jss.sh")
            shell.setExecutable(true)
            shell.setWritable(true)

            File art = new File(this.PackageNameWithPath)
            String fileName = art.getName()

            String args = sprintf(" -n %s -f %s -k %s -s %s -e %s -b %s", fileName, fileName,this.AccessKey,
                    this.SecretKey, this.CompilerOssEndpoint, this.CompilerOssBucket)

            this.script.echo "Start uploading..."
            this.script.sh("../meta/jss.sh" + args)
            this.script.echo "End uploading"
        }

    }

    def PrepareImage(){
        
        //Generate docker related Commands, we do this since docker login/rmi is not yet supported
        def login = sprintf("docker login -u jdcloud -p %s %s",this.DockerLoginToken,this.DockerRegistry)
        def buildCommand = sprintf("docker build -t %s:%s .",this.DockerRegistry,this.BuildTag)
        def pushCommand = sprintf("docker push %s:%s ",this.DockerRegistry,this.BuildTag)
        def rmiCommand = sprintf("docker rmi %s:%s ",this.DockerRegistry,this.BuildTag)

        // Start executing them
        this.script.dir(this.JenkinsWorkSpace){
            this.script.sh login
            this.script.sh buildCommand
            this.script.sh pushCommand
            this.script.sh rmiCommand
        }

    }

    def Execute(){

        CheckParameters()
        
        if(this.UploadArtifact == "1"){
  
            if(this.CompilerType == "Package"){
            
                Packaging()

                def hash = MD5Hash()

                RecordRuntimeEnv("COMPILER_PACKAGE_MD5SUM="+hash)

                UploadPackage()
            }

            if(this.CompilerType == "Image"){

                PrepareImage()

            }
        }else{
        
            this.script.echo "Uploading skipped"

        }

    }
}
