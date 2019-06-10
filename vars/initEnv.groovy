#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env) {

    env.JenkinsWorkSpace = "123"

    def initiating = new InitEnv(env.WORKSPACE,this)
    initiating.Execute()
}