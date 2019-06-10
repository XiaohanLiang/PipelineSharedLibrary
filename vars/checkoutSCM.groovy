#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    dir(env.UserWorkSpace){
        checkout changelog: false, poll: false,
                scm: [ $class: 'GitSCM', branches: [[name: env.ScmBranch]],
                       doGenerateSubmoduleConfigurations: false,
                       extensions: [],
                       submoduleCfg: [],
                       userRemoteConfigs: [[credentialsId: env.ScmCredential ,url: env.ScmUrl]]]
    }
}
