#!/usr/bin/env groovy
package com.jdcloud

import static org.junit.Assert.*
import java.lang.ProcessBuilder

class FromYaml {

    Map cmds
    Map envs
    String OutputSpace
    String metaspace
    Script script
    String toolChain

    def e
    def validTools = ["g":"/root/g"]
    def cachedImages = ["maven"]
    def SettingMap

    FromYaml (def env,Script s) {

        this.script = s
        this.metaspace = env.MetaSpace
        this.e = env
    }

    def Reader(){
        def reader = this.script.readYaml file: "${this.e.BuildYaml}"
        def r = reader.toString()
        def e = reader.envs.toString()
        this.script.echo r
        this.script.echo e
    }

    def GetYamlFile(){

        def jdcloudYaml = this.script.fileExists "${this.e.JdcloudYaml}"
        def buildYaml = this.script.fileExists "${this.e.BuildYaml}"

        if( !jdcloudYaml && !buildYaml ){
            this.script.error("Cannot find jdcloud-build.yml or build.yml")
        }

        if( jdcloudYaml ) {
            return "${this.e.JdcloudYaml}"
        }

        return "${this.e.BuildYaml}"
    }

    def GenerateShellScript(){

        if (this.e.USE_JDCLOUD_YAML=="1"){
            def y = GetYamlFile()
            this.SettingMap = this.script.readYaml file: y
        }

        if(this.e.USE_JDCLOUD_YAML != "1"){
            this.SettingMap = this.script.readYaml text: this.e.YAML
        }

        assertNotNull(this.SettingMap)

        cmds = [:]
        envs = [:]

        for( c in this.SettingMap.cmds ){
            this.cmds[c.name] = c.cmd
        }

        for ( ee in this.SettingMap.envs ) {
            this.envs[ee.name] = "'" + ee.value + "'"
        }

        if (this.SettingMap.out_dir == null) {
            this.OutputSpace = this.e.UserWorkSpace
        }else {
            this.OutputSpace = settingMap.out_dir
        }

        this.script.sh("#!/bin/sh -e\n touch ${this.metaspace}Jenkins-UserDefinedScripts.sh")
        this.script.sh("#!/bin/sh -e\n chmod +x ${this.metaspace}Jenkins-UserDefinedScripts.sh")
        def scriptPath = "${this.metaspace}Jenkins-UserDefinedScripts.sh"

        PrintWriter pencil = new PrintWriter(scriptPath)
        pencil.println("set -e")

        this.envs.each { name,value ->

            if (name == null || name.length()==0){
                return
            }
            pencil.println("echo 'SET Env \${" + name + "} = " + value + "'")
            pencil.println("export " + name + "=" + value)

        }

        this.cmds.each { name,cmd ->

            if (name == null || name.length()==0) {
                name = " (unnamed) "
            }
            if (cmd == null || cmd.length()==0) {
                return
            }

            pencil.println("echo Executing command: " + name)
            pencil.println("echo '\$ " + cmd + "'")
            pencil.println(cmd)
            pencil.println("echo -----")
            pencil.println("echo ''")

        }

        pencil.close()
        return scriptPath
    }

    def DefineRequirements(){

        // User defined yaml
        def args = generateAttachPair(e.MetaSpace)

        // TODO : Attach tools -> into /bin/<tool_name>:ro

        // Caches - For Java
        if (e.BUILD_IMAGE.toLowerCase().contains("maven") && !e.SCM_URL.contains("java-demo")){
            args += generateAttachPair(e.CacheSpace,"/root/.m2")
        }

        // Caches - For Android
        if (e.BUILD_IMAGE.toLowerCase().contains("gradle")){

            // Set Android SDK
            args += generateAttachPair("/usr/local/lib/android-sdk-linux","/usr/local/lib/android-sdk-linux","ro")
            args += generateEnvPair("ANDROID_HOME","/usr/local/lib/android-sdk-linux")

            // Set Android NDK
            args += generateAttachPair("/usr/local/lib/android-sdk-linux/ndk-bundle","/usr/local/lib/android-sdk-linux/ndk-bundle","ro")
            args += generateEnvPair("ANDROID_NDK_HOME","/usr/local/lib/android-sdk-linux/ndk-bundle")

            // Set Gradle Tool
            args += generateAttachPair("/usr/local/lib/gradle","/usr/local/lib/gradle","ro")
            args += generateEnvPair("GRADLE_HOME","/usr/local/lib/gradle")

            // Set JDK
            args += generateAttachPair("/usr/local/lib/jdk","/usr/local/lib/jdk","ro")
            args += generateEnvPair("JAVA_HOME","/usr/local/lib/jdk")

            // Set Android cache
            args += generateAttachPair(e.CacheSpace,"/cache/android/.gradle")
            args += generateEnvPair("GRADLE_USER_HOME","/cache/android/.gradle")

        }

        return args
    }

    def generateAttachPair(def source,def target=source,def pattern=""){
        def ret = (pattern == null || pattern.length()==0) ?
                sprintf("  -v %s:%s  ",source,target) :
                sprintf("  -v %s:%s:%s  ",source,target,pattern)
        return ret
    }
    def generateEnvPair(def key,def val){
        return sprintf(" -e %s=%s ",key,val)
    }

    def getOutputSpace(){
        return this.OutputSpace
    }

    def getYamlText(def env,Script s){
        def jdcloudYaml = new File(env.JdcloudYaml)
        if(jdcloudYaml.exists()){
            return jdcloudYaml.text
        }
        def buildYaml = new File(env.BuildYaml)
        if(buildYaml.exists()){
            return buildYaml.text
        }

        s.error("Cannot find jdcloud-build.yml or build.yml")
    }
}