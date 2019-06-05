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
 *          3. Continuous gain output
 *          4. Stop it from updating script every time
 */

class BuildYaml {

    Map commands
    Map environments
    String output
    Script script

    BuildYaml(String path,Script s) {

        Yaml yaml = new Yaml()
        def settingMap = yaml.load((path as File).text)
        assertNotNull(settingMap)

        commands = [:]
        environments = [:]

        for( c in settingMap.cmds ){
            this.commands[c.name] = c.command
        }
        for ( e in settingMap.envs ) {
            this.environments[e.name] = e.value
        }
        this.output = settingMap.out_dir == null ? "output" : settingMap.out_dir
        this.script = s
    }

    def Execute(){

        def scriptPath = this.WriteCommandsToShellScript()
//        this.ExecuteFile(scriptPath)
        return scriptPath
    }

    def WriteCommandsToShellScript(){

        File script = File.createTempFile("Jenkins-", ".sh");
        script.setExecutable(true)
        script.setWritable(true)
        script.deleteOnExit();
        def scriptPath = script.getAbsolutePath()
        this.script.echo "$scriptPath"

        PrintWriter pencil = new PrintWriter(scriptPath)

        this.environments.each { name,value ->

            if (name.length()==0){
                return
            }

            pencil.println("echo 'SET Env \${" + name + "} = " + value + "'")
            pencil.println("export " + name + "=" + value)
            pencil.println("echo -----")
            pencil.println("echo ''")

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

    // ----------------------------- We don't execute like this anymore

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
}