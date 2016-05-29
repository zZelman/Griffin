package com.griffin.core;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.prefs.*;

import org.ini4j.*;

import com.griffin.core.*;

public class ServerInfoParser {
    private final String IP = "ip";
    private final String PORT = "port";
    
    private String fileName;
    
    public ServerInfoParser(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public ServerInfo getServerInfo(String name) throws URISyntaxException, IOException, Exception {
        // TODO: move server_list.ini outside of the jar
        // URI uri = ClassLoader.getSystemClassLoader().getResource(this.fileName).toURI();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);

        // File file = new File(uri);
        Ini ini = new Ini(inputStream);
        Preferences prefs = new IniPreferences(ini);
        
        String ip = prefs.node(name).get(IP, null);
        String port = prefs.node(name).get(PORT, null);
        
        if (name == null ||
            ip == null ||
            port == null) {
            throw new Exception("Name or formating of the config file is incorrect. Cannot get [" + name + "] in " + this.fileName);
        }
        
        return new ServerInfo(name, ip, port);
    }
}
