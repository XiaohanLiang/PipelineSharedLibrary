#!/usr/bin/env groovy
import com.jdcloud.Address
import com.jdcloud.Cmds
import org.apache.ivy.ant.AddPathTask
import org.junit.Assert
@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

class ParseYaml{

    def Cmds c
    ParseYaml(String path){

//        Yaml yaml = new Yaml()
//        File file = new File(path)
//        FileInputStream stream = new FileInputStream(file);
//        this.c = yaml.loadAs(stream, Cmds.class)
    }

    def SetUp(){
        String yamlStr = "key: hello yaml";
        Yaml yaml = new Yaml();
        Object ret = yaml.load(yamlStr);
        return ret
    }

    def SetUp2(){
        Yaml yaml = new Yaml(new Constructor(Address.class))
        File file = new File("/root/bb.yaml")
        FileInputStream stream = new FileInputStream(file);
//        def c = yaml.loadAs(stream, Address.class)
        Address c = (Address) yaml.load(stream)
        return c
    }
}