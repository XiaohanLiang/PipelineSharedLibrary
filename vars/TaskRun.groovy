#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def pathToYaml,def env){

    def fromYaml = new FromYaml("/root/build.yaml",this, env.WORKSPACE)

    withDockerContainer(image:"ubuntu:14.04.5") {
        def scriptPath = fromYaml.GenerateShellScript()
        sh(scriptPath)
    }
}