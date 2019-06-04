package com.jdcloud

class OutputClass
{
    Script script;
    OutputClass(Script s)  // Have to pass the out variable to the class
    {
        this.script = s
        script.echo("Hello")
    }
}

