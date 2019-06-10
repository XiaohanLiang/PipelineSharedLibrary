#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    env.Branch = 'master'
    env.Url = "https://github.com/XiaohanLiang/hello"
    env.UserWorkSpace = env.WORKSPACE + "/workspace/"

    dir(env.UserWorkSpace){
        checkout changelog: false, poll: false,
                scm: [ $class: 'GitSCM', branches: [[name: env.Branch]],
                       doGenerateSubmoduleConfigurations: false,
                       extensions: [],
                       submoduleCfg: [],
                       userRemoteConfigs: [[url: env.Url]]]
    }
}
