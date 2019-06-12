#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){
    
    echo "We have metaSpace="
    echo env.MetaSpace
    def artifact = new ArtifactPackage(this,env)
    artifact.Execute()

}
