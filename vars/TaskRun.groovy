#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def pathToYaml,def env){

    def fromYaml = new FromYaml(env.Yaml,this, env.WORKSPACE)
    def scriptPath = fromYaml.GenerateShellScript()

    dir(env.UserWorkSpace){
        withDockerContainer(image:env.BuildImage) {
            sh(scriptPath)
        }
    }
}