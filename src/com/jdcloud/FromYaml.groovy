#!/usr/bin/env groovy
package com.jdcloud

//@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.Yaml
import static org.junit.Assert.*
import java.lang.ProcessBuilder

/**
 * Todo -
 *          Introduce some try....catch in creating and executing shells
 *
 * CheckList -
 *          1. ignore output, make it work locally - [ok]
 *          2. Gain output - [ok]
 *          3. Continuous gain output - [ok]
 *          4. Stop it from updating script every time
 */

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

    def GenerateShellScript(){

        Yaml yaml = new Yaml()

        if (this.e.USE_JDCLOUD_YAML=="1"){

            def jdcloudYaml = this.script.fileExists "${this.e.JdcloudYaml}"
            def buildYaml = this.script.fileExists "${this.e.BuildYaml}"

            if( !jdcloudYaml.exists() && !buildYaml.exists()){
                this.script.error("Cannot find jdcloud-build.yml or build.yml")
            }

            if( jdcloudYaml.exists() && !buildYaml.exists()) {
                def y = this.script.readFile "${this.e.JdcloudYaml}"
                this.SettingMap = yaml.load(y)
            }

            if( !jdcloudYaml.exists() && buildYaml.exists()) {
                def b = this.script.readFile "${this.e.BuildYaml}"
                this.SettingMap = yaml.load(buildYaml.text)
            }

            if( jdcloudYaml.exists() && buildYaml.exists()) {
                def y = this.script.readFile "${this.e.JdcloudYaml}"
                this.SettingMap = yaml.load(y)
            }

        }

        if(this.e.USE_JDCLOUD_YAML != "1"){
            this.SettingMap = yaml.load(this.e.YAML)
        }

        assertNotNull(settingMap)

        cmds = [:]
        envs = [:]

        for( c in settingMap.cmds ){
            this.cmds[c.name] = c.cmd
        }

        for ( ee in settingMap.envs ) {
            this.envs[ee.name] = "'" + ee.value + "'"
        }

        if (settingMap.out_dir == null) {
            this.OutputSpace = this.e.UserWorkSpace
        }else {
            this.OutputSpace = settingMap.out_dir
        }

        sh("touch ${this.metaspace}/Jenkins-UserDefinedScripts.sh")
//        File meta = new File(this.metaspace)
//        File script = File.createTempFile("Jenkins-UserDefinedScripts-", ".sh", meta);
//        script.setExecutable(true)
//        script.setWritable(true)
//        script.deleteOnExit();
//        def scriptPath = script.getAbsolutePath()
        def scriptPath = "${this.metaspace}/Jenkins-UserDefinedScripts.sh"

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