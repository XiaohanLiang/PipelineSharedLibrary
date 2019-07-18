#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def env){

    def fromYaml = new FromYaml(env,this)
    def scriptPath = fromYaml.GenerateShellScript()
    echo "scriptPath ${scriptPath}"
    def requirements = fromYaml.DefineRequirements()
    echo "requirements ${requirements}"
    env.OutputSpace = fromYaml.getOutputSpace()
    echo "OutputSpace ${env.OutputSpace}"

    dir(env.UserWorkSpace){
        withDockerContainer(args: requirements ,image:env.BUILD_IMAGE) {
            def s = sh(returnStatus:true ,script:scriptPath)
            if(s != 0){
                error("Failed in executing commands, exiting..")
            }
        }
    }
}