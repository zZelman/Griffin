package com.griffin.core.client;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import com.griffin.core.*;
import com.griffin.core.server.*;

public interface ClientCallBack {
    public void recieved(Object o);
    public void dealWith(ServerInfoException e);
    public void dealWith(ConnectException e);
    public void dealWith(UnknownHostException e);
    public void dealWith(ClassNotFoundException e);
    public void dealWith(IOException e);
    public void dealWithBadTarget(String target);
}
