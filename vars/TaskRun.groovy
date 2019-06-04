#!/usr/bin/env groovy
import com.jdcloud.*

/**
 *  This function is expected to output a structure namely
 *  `Tasks` containing Key-Value pair
 *
 *  Since this will be a structure. It must be somehow
 *  Connected to Java-Class, yes, output a Java-Class instance
 *
 */

def call(def pathToYaml){

    // Parse yaml and make it a class object
    def settings = new BuildYaml("/root/build.yaml")

    // Execute commands inside docker container
    withDockerContainer(args:"", image:"ubuntu:14.04.5") {
        settings.say()
    }
}