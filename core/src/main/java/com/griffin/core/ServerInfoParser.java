package com.griffin.core;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.prefs.*;

import org.ini4j.*;

import com.griffin.core.*;

public class ServerInfoParser {
    private final String HOST_NAME = "hostname";
    private final String PORT = "port";
    
    private String fileName;
    
    public ServerInfoParser(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public ServerInfo getServerInfo(String name) throws URISyntaxException, IOException, Exception {
        // TODO: move server_list.ini outside of the jar, prob some gradle config
        // URI uri = ClassLoader.getSystemClassLoader().getResource(this.fileName).toURI();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);

        // File file = new File(uri);
        Ini ini = new Ini(inputStream);
        Preferences prefs = new IniPreferences(ini);
        
        String hostName = prefs.node(name).get(HOST_NAME, null);
        int port = prefs.node(name).getInt(PORT, -1);
        
        if (name == null ||
            hostName == null ||
            port == -1) {
            throw new Exception("Name or formating of the config file is incorrect. Cannot get [" + name + "] in " + this.fileName);
        }
        
        return new ServerInfo(name, hostName, port);
    }
}
