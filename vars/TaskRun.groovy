#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def pathToYaml,def env){

    // Env.Yaml Location
    // Env.BuildImage

    echo "env.WORKSPACE = "
    echo env.WORKSPACE
    pwd

    def fromYaml = new FromYaml("/root/build.yaml",this, env.WORKSPACE)

    def scriptPath = fromYaml.GenerateShellScript()

    dir( env.WORKSPACE ){
        withDockerContainer(image:"ubuntu:14.04.5") {
            sh(scriptPath)
        }
    }
}