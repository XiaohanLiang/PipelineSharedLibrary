#!/usr/bin/env groovy

def call(String credential) {

  echo "Entered Shared Library"

  executeCommand("set +x")

  executeCommand("git init ${$WORKSPACE}/workspace")

  executeCommand("cd $WORKSPACE/workspace")

//  git config --local --unset credential.helper
//
//  mkdir -p $WORKSPACE/.JD_CODE_BUILD
//
//  git config credential.helper "store --file=$WORKSPACE/.JD_CODE_BUILD/.git-credentials"
//
//  env|grep 'COMPILER_\|GIT_\|WORKSPACE' > .JD_CODE_BUILD/buildRuntimeEnv
//
//  cat /var/tmp/REGION_ID >> .JD_CODE_BUILD/buildRuntimeEnv

}

def executeCommand(String command) {
  ['/bin/bash', '-c', ${command}].execute()
}