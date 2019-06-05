#!/usr/bin/env groovy

import com.jdcloud.*
import hudson.model.*

def call(def pathToYaml){

    // Parse yaml and make it a class object
    def settings = new BuildYaml("/root/build.yaml",this)


    // Execute commands inside docker container
    withDockerContainer(image:"ubuntu:14.04.5") {
//        settings.ExportEnvs()
        settings.Validate()
        sh "echo 123"
        settings.Execute()
        echo "2"
//        sh("echo \$nishizhu")
    }
}