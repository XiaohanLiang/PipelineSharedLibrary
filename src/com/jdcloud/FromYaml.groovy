#!/usr/bin/env groovy
package com.jdcloud

@Grab(group='org.yaml', module='snakeyaml', version='1.17')
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

    Map commands
    Map environments
    String OutputSpace
    String metaspace
    Script script
    String toolChain
    def e
    def validTools = ["g":"/root/g"]
    def cachedImages = ["maven"]

    FromYaml (def env,Script s) {

        Yaml yaml = new Yaml()
        def settingMap = yaml.load(env.YAML)
        assertNotNull(settingMap)

        commands = [:]
        environments = [:]
        for( c in settingMap.cmds ){
            s.echo "Inside cmd map"
            s.echo "this time the name"
            s.echo c.name
            s.echo "this time the command"
            s.echo c.command
            this.commands[c.name] = c.command
        }
        for ( ee in settingMap.envs ) {
            s.echo "Inside Setting map"
            this.environments[ee.name] = ee.value
        }
        if (settingMap.out_dir == null) {
            s.echo "Yes outdir is empty"
            this.OutputSpace = env.UserWorkSpace
        }else {
            s.echo "Nah outdir not empty"
            this.OutputSpace = settingMap.out_dir
        }
        this.metaspace = env.MetaSpace
        //this.toolChain = env.Tools
        this.e = env
        this.script = s
    }

    def GenerateShellScript(){

        File meta = new File(this.metaspace)
        File script = File.createTempFile("Jenkins-UserDefinedScripts-", ".sh", meta);
        script.setExecutable(true)
        script.setWritable(true)
        script.deleteOnExit();
        def scriptPath = script.getAbsolutePath()

        PrintWriter pencil = new PrintWriter(scriptPath)

        this.script.echo "this.environments.each pre"
        this.environments.each { name,value ->

            this.script.echo "this.environments.each post"
            if (name.length()==0){
                return
            }

            pencil.println("echo 'SET Env \${" + name + "} = " + value + "'")
            pencil.println("export " + name + "=" + value)
        }

        this.script.echo "this.commands.each pre"
        this.commands.each { name,command ->
        this.script.echo "this.commands.each post"

            if (name.length()==0) {
                name = " (unnamed) "
            }
            if (command.length()==0) {
                return
            }

            pencil.println("echo Executing command: " + name)
            pencil.println("echo '\$ " + command + "'")
            pencil.println(command)
            pencil.println("echo -----")
            pencil.println("echo ''")

        }

        pencil.close()
        return scriptPath
    }

    def DefineRequirements(){

        // User defined yaml
        def args = generateReqPair("-v",e.MetaSpace)

        // Tools : If desired tools are valid, then we map it into /bin/<Your_tool>:ro
        for( tool in this.toolChain.split("#")){
            if (validTools.containsKey(tool)){
                args += generateReqPair("-v",validTools.get(tool),"/bin/"+tool,"ro")
            }
        }

        // Caches
        if (e.BuildImage.toLowerCase().contains("maven")){
            args += generateReqPair("-v",e.CacheSpace,"/root/.m2")
        }

        return args
    }

    def generateReqPair(def type,def source,def target=source,def pattern=""){
        def ret = pattern.length()==0 ? sprintf("  %s %s:%s  ",type,source,target) :
                                        sprintf("  %s %s:%s:%s  ",type,source,target,pattern)
        return ret
    }

    def getOutputSpace(){
        return this.OutputSpace
    }

    // ----------------------------- We don't execute file here considering tricky IO issue
    def ExecuteCommandsUsingExecute(){

        this.commands.each { name,command ->

            if (name.length()==0 || command.length()==0) {
                return
            }

            this.script.echo "Executing command: " + name
            this.script.echo "\$      " + command

            def Stdout = new StringBuilder()
            def Stderr = new StringBuilder()
            def start = command.execute()
            start.consumeProcessOutput(Stdout, Stderr)
            start.waitForOrKill(3600 * 1000)

            this.script.echo ">      $Stdout"
            this.script.echo "------------"

        }
    }
    def ExecuteCommandsUsingExecute2(def command){

            this.script.echo "Executing command: " + command

            def Stdout = new StringBuilder()
            def Stderr = new StringBuilder()
            def start = command.execute()
            start.consumeProcessOutput(Stdout, Stderr)
            start.waitForOrKill(3600 * 1000)

            this.script.echo ">      $Stdout"
            this.script.echo "------------"

    }
    def ExecuteCommandsUsingProcessBuilder(def command){
        ProcessBuilder processBuilder = new ProcessBuilder("bash","-c",command);
        processBuilder.redirectErrorStream(true)
        System.out.println("Run echo command");
        Process process = processBuilder.start();
        int errCode = process.waitFor();
        System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
        System.out.println("Echo Output:\n" + process.getInputStream().text);
    }
    def output(InputStream inputStream) throws IOException {
        if (inputStream == null ) {
            this.script.echo "is null"
        }else{
            this.script.echo "not null"
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                this.script.echo "appending"
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        this.script.echo "--"
        this.script.echo sb.toString()
        this.script.echo "--"

        if (sb == null) {
            this.script.echo "sb null"
        }else{
            this.script.echo "sb not null"
        }

        this.script.echo "--"
        return sb.toString();
    }
    def ExportEnvs() {

        this.environments.each { name,value ->

            if (name.length()==0) {
                return
            }

            def SetEnvCommand = "export " + name + "=" + value
            this.script.echo "SET Env \${" + name + "} = " + value
            this.script.echo SetEnvCommand

            def Stdout = new StringBuilder()
            def Stderr = new StringBuilder()
            def start = SetEnvCommand.execute()
            start.consumeProcessOutput(Stdout, Stderr)
            start.waitForOrKill(1000)

        }

    }
    def ExecuteFile(def filePath){

        ProcessBuilder processBuilder = new ProcessBuilder(filePath)
        processBuilder.redirectErrorStream(true)

        Process process = processBuilder.start()
        process.waitFor()
        def consoleOutPut = process.getInputStream().text
        this.script.echo "$consoleOutPut"

    }
    def GainOutput(def instream){



    }

}