#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def pathToYaml,def env){

    env.Yaml = "---"
    env.BuildImage = "ubuntu:14.04.5"
    env.UserWorkSpace = env.WORKSPACE + "/workspace"

    def fromYaml = new FromYaml(env.Yaml,this, env.WORKSPACE)
    def scriptPath = fromYaml.GenerateShellScript()

    dir(env.UserWorkSpace){
        withDockerContainer(image:env.BuildImage) {
            sh(scriptPath)
        }
    }
}