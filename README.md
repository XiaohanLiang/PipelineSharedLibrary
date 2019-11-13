# PipelineSharedLibrary

We were planning to replace 'BuildCMD', with a decent way to compile, build, eventually profiling artifact.
Making stages and functions in JenkinsFile more general purpose. Thus we would like to introduce Pipeline shared library.

## This is a beta version

There weren't too many function in our 'Cloud-Platform-Compile' at this point. We plan to add the following function in 
the near future.

- Init environment: Making paths, git credentials
- Unmarshall yaml files, execute shell commands inside
- Upload to AWS S3 Bucket with signing 
- Docker images managing
- Added
