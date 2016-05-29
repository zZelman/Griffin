package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;

import com.griffin.core.*;

public class Main {
    public static void main(String[] args) {
        ServerInfoParser parser = new ServerInfoParser("server_list.ini");
        ServerInfo info = null;
        
        try {
            info = parser.getServerInfo("desktop");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(info);
    }
}
