#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){

    def artifact = new Artifact(env,this)
    artifact.Execute()

}
