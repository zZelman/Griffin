package com.griffin.core;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class TextCommunication {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    public TextCommunication(Socket socket) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String s) {
        this.out.println(s);
    }

    public String receive() throws IOException {
        return this.in.readLine();
    }
}
