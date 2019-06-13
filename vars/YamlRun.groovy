#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def env){

    def scriptPath = fromYaml.GenerateShellScript()
    def requirements = fromYaml.DefineRequirements()

    dir(env.UserWorkSpace){
        withDockerContainer(args: requirements ,image:env.BuildImage) {
            sh(scriptPath)
        }
    }
}