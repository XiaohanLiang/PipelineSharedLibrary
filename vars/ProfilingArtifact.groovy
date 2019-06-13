#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){

    def artifact = new ArtifactPackage(env,this)
    artifact.Execute()

}
