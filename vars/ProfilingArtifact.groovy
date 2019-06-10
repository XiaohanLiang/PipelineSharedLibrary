#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){

    def artifact = new ArtifactPackage(this,env)
    artifact.Execute()

}
