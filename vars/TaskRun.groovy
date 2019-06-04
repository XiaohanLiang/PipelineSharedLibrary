#!/usr/bin/env groovy
import com.jdcloud.Cmds
import com.jdcloud.ParseYaml
import com.jdcloud.Address

/**
 *  This function is expected to output a structure namely
 *  `Tasks` containing Key-Value pair
 *
 *  Since this will be a structure. It must be somehow
 *  Connected to Java-Class, yes, output a Java-Class instance
 *
 */

def call(def pathToYaml){

    // Parse yaml and make it Java class
    echo "1"
    ParseYaml py = new ParseYaml(pathToYaml)
    def item = py.SetUp2()
    echo "2"
    println(item)
    def item2 = py.SetUp2()
    echo "2"
    println(item)
    def d = (com.jdcloud.Address)item2
    println(d)
//    echo "2"
//    cmds.SetUp(pathToYaml)
//    echo "3"

    // Generate exporting commands
//    cmds.PreprareEnvs()
//
    // Running inside Docker
//    withDockerContainer(args:"", image:params.buildImage) {
//        cmds.Execute()
//    }
}