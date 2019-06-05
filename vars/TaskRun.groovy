#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def pathToYaml){

    // Parse yaml and make it a class object
    def settings = new BuildYaml("/root/build.yaml",this)

    // Execute commands inside docker container
    withDockerContainer(image:"ubuntu:14.04.5") {
//        settings.ExportEnvs()
        sh "echo 123"
        def scriptPath = settings.Execute()
        sh("pwd")
        sh("cd /tmp")
        sh("ls")

        sh(scriptPath)
//        sh("echo \$nishizhu")
    }
}