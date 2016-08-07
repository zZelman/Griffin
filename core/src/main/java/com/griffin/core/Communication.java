package com.griffin.core;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class Communication {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public Communication(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }
    
    public void send(Serializable s) throws IOException {
        this.out.writeObject(s);
    }
    
    public Object receive() throws ClassNotFoundException, IOException {
        Object o = null;
        try {
            o = this.in.readObject();
        } catch (EOFException e) {}
        
        return o;
    }
    
    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }

    public String getRemoteAddr() {
        return this.socket.getRemoteSocketAddress().toString();
    }

    public String getLocalAddr() {
        return this.socket.getLocalAddress().toString() + ":" + this.socket.getLocalPort();
    }
}
