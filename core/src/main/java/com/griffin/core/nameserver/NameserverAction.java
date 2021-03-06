package com.griffin.core.nameserver;

import java.io.*;

import com.griffin.core.*;
import com.griffin.core.server.*;

// only visable inside of the package
class NameserverAction implements Serializable {
    public enum Action {
        PING,  // Server just came online, tell Nameserver to update w/ given info
        GET,   // Client is asking for info about the given target
        LIST,  // Client is asking for all information about everyone
        HELP,  // Client does not know what to do, give use info
        STOP   // Client requests that the Nameserver stop
    }
    
    private Action action = null;
    private ServerInfo info = null;
    private String target = null;
    
    public NameserverAction(Action action) {
        this.action = action;
    }
    
    public NameserverAction(ServerInfo info) {
        this.action = Action.PING;
        this.info = info;
    }
    
    public NameserverAction(String target) {
        this.action = Action.GET;
        this.target = target;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public ServerInfo getInfo() {
        return this.info;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public String toString() {
        return "Action:\n" +
               "    " + this.action.name() + "\n" +
               /*    */ this.info.toString() + "\n" +
               "Target:\n" +
               "    " + this.target;
    }
}
