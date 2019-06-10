#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    env.ScmBranch = 'master'
    env.ScmUrl = "https://github.com/XiaohanLiang/hello"
    env.ScmCredential = ""
    env.UserWorkSpace = env.WORKSPACE + "/workspace/"

    dir(env.UserWorkSpace){
        checkout changelog: false, poll: false,
                scm: [ $class: 'GitSCM', branches: [[name: env.ScmBranch]],
                       doGenerateSubmoduleConfigurations: false,
                       extensions: [],
                       submoduleCfg: [],
                       userRemoteConfigs: [[credentialsId: env.ScmCredential ,url: env.ScmUrl]]]
    }
}
