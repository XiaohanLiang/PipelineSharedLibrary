#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env){

    dir(env.UserWorkSpace){

        writeFile file: "${env.MetaSpace}scm.sh", text: """
        #!/bin/bash
        set +xe
        git init ${env.UserWorkSpace} 
        cd ${env.UserWorkSpace} 
        git config --local --unset credential.helper
        mkdir -p ${env.MetaSpace}
        git config credential.helper "store --file=${env.MetaSpace}.git-credentials"
        echo ${env.SCM_CREDENTIAL} > ${env.MetaSpace}.git-credentials
        """

        sh(returnStdout: true,script:"${env.MetaSpace}scm.sh")

        checkout changelog: false, poll: false,
                scm: [ $class: 'GitSCM', branches: [[name: "${env.SCM_BRANCH}"]],
                       doGenerateSubmoduleConfigurations: false,
                       extensions: [],
                       submoduleCfg: [],
                       userRemoteConfigs: [[url: "${env.SCM_URL}"]]]

    }

}
