package com.griffin.core;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class TextCommunication {
    private PrintWriter out;
    private BufferedReader in;
    
    private final int bufferSize = 2000000;
    
    public TextCommunication(Socket socket) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void send(String s) {
        this.out.print(s);
        this.out.flush();
    }
    
    public String receive() throws IOException {
        char[] recieveBuffer = new char[this.bufferSize];
        int read = this.in.read(recieveBuffer, 0, this.bufferSize);
        if (read == -1) {
            return null;
        }
        
        return new String(recieveBuffer);
    }
}
