#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){

    // Initiating env here

    def artifact = new ArtifactPackage(env.WORKSPACE,"1","MyModuleName","master","bucketName","bucketPath","endpoint","ak","sk")
    artifact.Execute()

}
