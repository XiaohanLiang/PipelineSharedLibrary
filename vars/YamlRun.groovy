#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def env){

    def fromYaml = new FromYaml(env,this)
    def scriptPath = fromYaml.GenerateShellScript()
    def requirements = fromYaml.DefineRequirements()
    env.OutputSpace = fromYaml.getOutputSpace()

    dir(env.UserWorkSpace){
        withDockerContainer(args: requirements ,image:env.BUILD_IMAGE) {
            sh(script:scriptPath)
            //def status = sh(returnStdout: true,script:scriptPath)
            //echo "we have status" + status
        }
    }
}