package com.griffin.core;

import java.io.*;
import java.lang.*;
import java.net.*;

import com.griffin.core.*;

public interface ServerCallBack {
    public void startedServerSocket(ServerSocket serverSocket);
    public void serverInfo(ServerInfo info);
    public void taskList(String s);
    public void startedConnection();
    public void commandRecieved(String s);
    public void serverEnding(String s);
    public void dealWith(ClassNotFoundException e);
    public void dealWith(IOException e);
}
