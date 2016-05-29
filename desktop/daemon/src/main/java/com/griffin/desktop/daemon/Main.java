package com.griffin.desktop.daemon;

import com.griffin.core.*;

public class Main {
    public static void main(String[] args) {
        Griffin g = new Griffin(new ConcreteTaskFactory());

        String command = "print commands";
        String ret = g.doCommand(command);

        System.out.println(ret);
    }
}
