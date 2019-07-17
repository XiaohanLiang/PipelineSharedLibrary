#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    dir(env.UserWorkSpace){

        sh """
            set +e
            git init 
            git config --local --unset credential.helper -vvv
            git config credential.helper store --file=${env.MetaSpace}.git-credentials
            echo 'https://oauth2:c06651fe24e7b48030abe1f853a7c7fe6e1a43f2@github.com' > ${env.MetaSpace}.git-credentials
        """
//
//        checkout changelog: false, poll: false,
//                scm: [ $class: 'GitSCM', branches: [[name: env.SCM_BRANCH]],
//                       doGenerateSubmoduleConfigurations: false,
//                       extensions: [],
//                       submoduleCfg: [],
//                       userRemoteConfigs: [[url: env.SCM_URL]]]
        git branch: "${env.SCM_BRANCH}", url: "${env.SCM_URL}"

    }
}
