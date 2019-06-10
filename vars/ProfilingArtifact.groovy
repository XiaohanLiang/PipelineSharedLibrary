#!/usr/bin/env groovy

import com.jdcloud.*

def call(def env){

    def artifact = new ArtifactPackage(
            this,
            env.WORKSPACE,
            env.OutputSpace,
            env.UploadArtifact,
            env.CompileModuleName,
            env.ScmBranch,
            env.CommitID,
            env.OssBucketName,
            env.OssBucketpath,
            env.OssBucketEndpoint,
            env.OssAccessKey,
            env.OssSecretKey)

    artifact.Execute()

}
