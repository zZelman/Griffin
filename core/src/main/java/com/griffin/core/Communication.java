package com.griffin.core;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class Communication {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public Communication(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    
    public void send(Serializable s) throws IOException {
        this.out.writeObject(s);
        this.out.flush();
    }
    
    public Object receive() throws ClassNotFoundException, IOException {
        Object o = null;
        try {
            o = this.in.readObject();
        } catch (EOFException e) {}
        
        return o;
    }
    
    public void close() throws IOException {
        this.out.close();
        this.in.close();
    }
}
