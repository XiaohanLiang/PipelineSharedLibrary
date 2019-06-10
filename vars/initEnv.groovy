#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env) {

    def initiating = new InitEnv(env.WORKSPACE,this)
    initiating.Execute()
}