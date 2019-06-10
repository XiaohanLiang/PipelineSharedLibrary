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
    String output
    String metaspace
    Script script

    FromYaml (def e,Script s) {

        Yaml yaml = new Yaml()
        def settingMap = yaml.load(e.Yaml)
        assertNotNull(settingMap)

        commands = [:]
        environments = [:]
        for( c in settingMap.cmds ){
            this.commands[c.name] = c.command
        }
        for ( ee in settingMap.envs ) {
            this.environments[ee.name] = ee.value
        }

        this.output = e.OutputSpace
        this.metaspace = e.MetaSpace
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

        this.environments.each { name,value ->

            if (name.length()==0){
                return
            }

            pencil.println("echo 'SET Env \${" + name + "} = " + value + "'")
            pencil.println("export " + name + "=" + value)
        }

        this.commands.each { name,command ->

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