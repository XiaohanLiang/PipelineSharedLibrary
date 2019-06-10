#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){

    // Initiating env here

    def artifact = new ArtifactPackage(this,env.WORKSPACE,env.WORKSPACE,"1","MyModuleName","master","abc123xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx","bucketName","bucketPath","endpoint","ak","sk")
    artifact.Execute()

}
