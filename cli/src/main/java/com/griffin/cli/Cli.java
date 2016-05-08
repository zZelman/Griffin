package com.griffin.cli;

import com.griffin.core.*;

public class Cli {
    public static void main(String[] args) {
        String command = "hello world";
        
        Griffin g = new Griffin();
        
        // check for command
        boolean exists = g.doesCommandExist(command);
        if (!exists) {
            String noExistError = g.getNoExistErrorMsg();
            System.out.println(noExistError + command);
            return;
        }
        
        // say the command is about to start
        String startingTaskMsg = g.getStartingMsg();
        System.out.println(startingTaskMsg);
        
        // execute the task
        String returnMsg = g.doTask(command);
        
        // say the return value of the task
        System.out.println(returnMsg);
        
        // say all tasks have been completed
        String endingMsg = g.getEndingMsg();
        System.out.println(endingMsg);
    }
}
