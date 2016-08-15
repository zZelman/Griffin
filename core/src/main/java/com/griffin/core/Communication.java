package com.griffin.core;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class Communication {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    // this constructor (+ the 'if nulls') allow for the Communication class to munch input/output
    public Communication() {
    }
    
    public Communication(Socket socket) throws IOException {
        this.socket = socket;
        this.init();
    }
    
    public Communication(String hostName, int port) throws IOException {
        this.socket = new Socket(hostName, port);
        this.init();
    }
    
    private void init() throws IOException {
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    public boolean isHeadless() {
        return (this.in == null || this.out == null);
    }
    
    public void send(Serializable s) throws IOException {
        if (this.out == null) {
            return;
        }
        this.out.writeObject(s);
    }
    
    public Object receive() throws ClassNotFoundException, IOException {
        if (this.in == null) {
            return null;
        }
        
        Object o = null;
        try {
            o = this.in.readObject();
        } catch (EOFException e) {}
        
        return o;
    }
    
    public void close() throws IOException {
        if (this.in == null ||
            this.out == null ||
            this.socket == null) {
            return;
        }
        
        this.in.close();
        this.out.close();
        this.socket.close();
    }
    
    public String getRemoteAddr() {
        if (this.socket == null) {
            return null;
        }
        
        return this.socket.getRemoteSocketAddress().toString();
    }
    
    public String getLocalAddr() {
        if (this.socket == null) {
            return null;
        }
        
        return this.socket.getLocalAddress().toString() + ":" + this.socket.getLocalPort();
    }
}
