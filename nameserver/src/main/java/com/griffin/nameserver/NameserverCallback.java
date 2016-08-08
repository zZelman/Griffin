package com.griffin.nameserver;

import java.net.*;
import java.io.*;

public interface NameserverCallback {
    public void nameserverException(UnknownHostException e);
    public void nameserverException(IOException e);
}
