#!/usr/bin/env groovy
import java.io.File

/**
 * Todo -
 *        nil
 *
 */
def call(def path,def depth="ALL"){

    assert path.length()>0 :"Empty path given"

    def ws = path + "/workspace"
    def meta = path + "/.JD_CODE_BUILD"
    def entranceExists = fileExists path
    def wsExists = fileExists ws
    def metaExists = fileExists meta

    if (entranceExists) {

        echo "Cleaning workspace.."

        if ( depth == "ALL" && wsExists ) {
            ws.deleteDir()
        }

        if (metaExists){
            meta.deleteDir()
        }
    }
}
