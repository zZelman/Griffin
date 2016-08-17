package com.griffin.core.server;

import java.io.*;
import java.lang.*;
import java.net.*;

import com.griffin.core.*;

public interface ServerCallBack {
    public void startedServerSocket(ServerSocket serverSocket);
    public void serverInfo(ServerInfo info);
    public void taskList(String taskList);
    public void commandRecieved(String datetime, String remoteAddr, String localAddr, String command);
    public void serverEnding(String msg);
    public void dealWith(ClassNotFoundException e);
    public void dealWith(IOException e);
}
