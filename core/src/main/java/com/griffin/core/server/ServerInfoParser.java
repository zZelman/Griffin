package com.griffin.core.server;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.prefs.*;

import org.ini4j.*;

import com.griffin.core.*;

public class ServerInfoParser {
    private final String NAMESERVER = "nameserver";
    
    private final String HOST_NAME = "hostname";
    private final String PORT = "port";
    
    private IniPreferences iniPrefs;
    
    public ServerInfoParser(String fileName) throws FileNotFoundException, IOException {
        this.init(new FileInputStream(fileName));
    }
    
    public ServerInfoParser(InputStream inputStream) throws IOException {
        this.init(inputStream);
    }
    
    private void init(InputStream inputStream) throws IOException {
        Ini ini = new Ini(inputStream);
        this.iniPrefs = new IniPreferences(ini);
        inputStream.close();
    }

    public ServerInfo getNameserverInfo() throws ServerInfoException {
        return this.getServerInfo(NAMESERVER);
    }
    
    public ServerInfo getServerInfo(String name) throws ServerInfoException {
        // TODO: move server_list.ini outside of the jar, prob some gradle config
        // URI uri = ClassLoader.getSystemClassLoader().getResource(this.fileName).toURI();
        // InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);
        
        String hostName = this.iniPrefs.node(name).get(HOST_NAME, null);
        int port = this.iniPrefs.node(name).getInt(PORT, -1);
        
        if (name == null ||
            hostName == null ||
            port == -1) {
            throw new ServerInfoException("name or formating of the info file is incorrect. name=["+name+"], hostName=["+hostName+"], port=["+port+"]");
        }
        
        return new ServerInfo(name, hostName, port);
    }
}
