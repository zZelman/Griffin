package com.griffin.core;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import com.griffin.core.*;

public interface ClientCallBack {
    public void recieved(Object o);
    public void dealWith(ConnectException e);
    public void dealWith(UnknownHostException e);
    public void dealWith(ClassNotFoundException e);
    public void dealWith(IOException e);
}
