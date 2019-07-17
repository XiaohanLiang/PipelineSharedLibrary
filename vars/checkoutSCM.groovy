#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    dir(env.UserWorkSpace){

        sh """

            git init ${env.UserWorkSpace}
            git config --local --unset credential.helper
            git config credential.helper store --file=${env.MetaSpace}.git-credentials
            echo ${env.SCM_CREDENTIAL} > ${env.MetaSpace}.git-credentials
        """

        checkout changelog: false, poll: false,
                scm: [ $class: 'GitSCM', branches: [[name: env.SCM_BRANCH]],
                       doGenerateSubmoduleConfigurations: false,
                       extensions: [],
                       submoduleCfg: [],
                       userRemoteConfigs: [[url: env.SCM_URL]]]
    }
}
