#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    dir(env.UserWorkSpace){
        checkout changelog: false, poll: false,
                scm: [ $class: 'GitSCM', branches: [[name: env.SCM_BRANCH]],
                       doGenerateSubmoduleConfigurations: false,
                       extensions: [],
                       submoduleCfg: [],
                       userRemoteConfigs: [[credentialsId: env.SCM_CREDENTIAL ,url: env.SCM_URL]]]
    }
}
