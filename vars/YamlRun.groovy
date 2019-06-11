#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def env){

    def fromYaml = new FromYaml(env,this)
    def scriptPath = fromYaml.GenerateShellScript()
    def requirements = fromYaml.DefineRequirements()

    dir(env.UserWorkSpace){
        withDockerContainer(args: requirements ,image:env.BuildImage) {
            sh(scriptPath)
        }
    }
}