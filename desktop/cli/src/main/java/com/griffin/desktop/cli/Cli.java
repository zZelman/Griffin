package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.nameserver.*;

public class Cli implements ClientCallBack, Startable {
    private Client client;
    
    public Cli(ServerInfoParser infoParser, String target, Serializable command) {
        this.client = new Client(this, infoParser, target, command);
    }
    
    @Override
    public void recieved(Object o) {
        if (o instanceof Output) {
            this.printOutput((Output) o);
        } else {
            this.println(o.toString()); // a catch-all for unexpected output (like "prev comm"'s string)
        }
    }
    
    @Override
    public void dealWith(ServerInfoException e) {
        this.println(e);
    }
    
    @Override
    public void dealWith(ConnectException e) {
        this.println(e);
    }
    
    @Override
    public void dealWith(UnknownHostException e) {
        this.println(e);
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.println(e);
    }
    
    @Override
    public void dealWith(IOException e) {
        this.println(e);
    }
    
    @Override
    public void dealWithBadTarget(String target) {
        this.println("nameserver did not have an entery for: " + target);
    }
    
    @Override
    public boolean start() {
        return this.client.start();
    }
    
    @Override
    public boolean stop() {
        return this.client.stop();
    }
    
    private void printOutput(Output o) {
        this.printOutput(0, o);
    }
    
    private void printOutput(int indentLevel, Output curr) {
        // deal with given
        this.doPrint(indentLevel, curr);
        if (curr.hasSubtaskOutput()) {
            this.printOutput(++indentLevel, curr.getSubtaskOutput());
        }
        
        while (curr.hasNext()) {
            curr = curr.next();
            this.doPrint(indentLevel, curr);
            if (curr.hasSubtaskOutput()) {
                this.printOutput(indentLevel + 1, curr.getSubtaskOutput());
            }
        }
    }
    
    private void doPrint(int indentLevel, Output o) {
        String indentStr = "";
        for (int i = 0; i < indentLevel; ++i) {
            indentStr += "|   ";
        }
        
        if (o instanceof StringOutput) {
            StringOutput so = (StringOutput) o;
            
            // this.println(indentStr + so); // this shows the data aswell as the object type
            this.println(indentStr + so.getString()); // just shows the data
        }
        // else {
        //     this.println(o); // this is a catch-all that will display with type the recieved
        // }
    }
    
    private void println(Object o) {
        this.println(o.toString());
    }
    
    private void println(String s) {
        System.out.println(s);
    }
    
    private void println(Exception e) {
        e.printStackTrace();
    }
    
    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_info_filename] [target] [command...]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length < 3) {
            Cli.usage();
        }
        
        String fileName = args[0];
        String target = args[1];
        
        String[] commandTokens = ArrayUtils.subarray(args, 2, args.length);
        Serializable command = StringUtils.join(commandTokens, " ");
        
        ServerInfoParser infoParser = null;
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
            infoParser = new ServerInfoParser(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        Cli cli = new Cli(infoParser, target, command);
        cli.start();
        cli.stop();
    }
}
