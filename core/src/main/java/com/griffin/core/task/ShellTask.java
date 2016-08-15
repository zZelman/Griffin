package com.griffin.core.task;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.task.*;

public class ShellTask extends Task {
    private String shellCommand;
    
    public ShellTask() {
        super("shell [command...]",
              "runs the [command...] in a shell");
    }
    
    @Override
    public String canUse(String rawInput) {
        String space = "( )";
        
        String shell = "(shell)";
        String shellCommand = "(.+)";
        
        Pattern p = Pattern.compile(shell + space + shellCommand, Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.shellCommand = m.group(3);
            
            this.setRuntimeCommand("shell " + this.shellCommand);
            return rawInput.replaceFirst(shell + space + shellCommand, "");
        }
        return null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.getRuntimeCommand());
        
        try {
            ArrayList<String> out = this.helper(this.shellCommand, ".");
            if (out == null) {
                output.addOutput(new ErrorOutput("bad command, or some other failure"));
            } else {
                for (String s : out) {
                    output.addOutput(new StringOutput(s));
                }
            }
        } catch (IOException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        } catch (InterruptedException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        }
        
        if (output.containsError()) {
            output.addOutput(new FailureOutput(this.failure));
        } else {
            output.addOutput(new SuccessOutput(this.success));
        }
        
        return output;
    }
    
    @Override
    public void clear() {
        this.resetRuntimeCommand();
        this.shellCommand = "";
    }
    
    private ArrayList<String> helper(final String cmdline, final String directory) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(new String[] {"bash", "-c", cmdline})
        .redirectErrorStream(true)
        .directory(new File(directory))
        .start();
        
        ArrayList<String> output = new ArrayList<String>();
        String line;
        
        BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = bri.readLine()) != null) {
            output.add(line);
        }
        bri.close();
        
        BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = bre.readLine()) != null) {
            output.add(line);
        }
        bre.close();
        
        // There should really be a timeout here.
        if (0 != process.waitFor()) {
            return null;
        }
        
        return output;
    }
}
