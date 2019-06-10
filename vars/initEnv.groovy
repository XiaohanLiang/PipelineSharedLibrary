#!/usr/bin/env groovy
import com.jdcloud.*

def call(def env) {

    env.JenkinsWorkSpace = env.WORKSPACE
    env.UserWorkSpace = env.WORKSPACE + "/workspace/"
    env.ArtifactSpace = env.WORKSPACE + "/artifact/"
    env.MetaSpace = env.WORKSPACE + "/meta/"
    env.CacheSpace = env.WORKSPACE + "/cache/"
    env.RuntimeEnv = env.WORKSPACE + "/meta/buildRuntimeEnv"

    env.ScmUrl = "https://github.com/XiaohanLiang/hello"
    env.ScmBranch = 'master'
    env.CommitID = ""
    env.ScmCredential = ""

    env.Yaml = "---"
    env.BuildImage = "ubuntu:14.04.5"

    env.UploadArtifact = ""
    env.CompileModuleName = ""
    env.OutputSpace = ""
    env.OssBucketName = ""
    env.OssBucketpath = ""
    env.OssBucketEndpoint = ""
    env.OssAccessKey = ""
    env.OssSecretKey = ""

    def initiating = new InitEnv(env, this)
    initiating.Execute()
}