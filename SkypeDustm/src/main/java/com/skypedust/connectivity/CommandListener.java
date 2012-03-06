/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skypedust.connectivity;

import com.skype.api.Conversation;
import com.skype.api.Message;
import com.skype.api.Message.MessageListener;
import com.skype.api.Message.PROPERTY;
import com.skype.api.SkypeObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.skypedust.connectivity.UberClient;
import com.skypedust.util.MySession;

/**
 *
 * @author carnage
 */
public class CommandListener {

    private List<String> commandcons;
    private UberClient uberClient;
    
    public CommandListener(String[] contacts){
        
        commandcons = new ArrayList<>();
        for(String con : contacts){
            commandcons.add(con);
        }
        
        uberClient = new UberClient("http://uberdust.cti.gr/rest/testbed/1");
    }
    
    public String messageParse(String author,String Body){    
        
        /*
         * Just for start
         */
        
        if(commandcons.contains(author))
        {
            String toret = "Unregognized command please type help to see your options";
            
            
            String[] commands = Body.split(" ");
            
            if(commands.length==4){
            
                if((commands[0]=="node")&(commands[1]=="temperature")&(commands[2]=="set")){
                    toret = "setting the node temperature";
                }

            }
            else if(commands.length==3){

                if("node".equals(commands[0])){
                    switch(commands[2]){
                        case "parking":
                            break;
                        case "noise":
                            break;
                        case "light":
                            System.out.println("Loading");
                            toret = uberClient.getnodeLight(commands[1],1);
                            break;
                        case "temperature":
                            toret = uberClient.getnodeTemperature(commands[1],1);
                            break;
                        case "pir":
                            break;
                        case "barometricpressure":
                            break;
                        case "humidity":
                            break;
                        case "ir":
                            break;
                        case "batterycharge":
                            break;
                        case "co":
                            break;
                        case "co2":
                            break;
                        case "ch4":
                            break;
                        case "light4":
                            break;
                        case "light2":
                            break;
                        case "light1":
                            break;
                        case "pressure":
                            break;
                        case "lockScreen":
                            break;
                        case "status":
                            break;
                        case "status1":
                            break;
                        case "command":
                            break;
                        case "operation":
                            break;
                        case "payload":
                            break;
                    }
                }
                if("help".equals(commands[0])){
                    switch(commands[1]){
                        case "node":
                            
                            break;
                    } 
                }
            }
            else if(commands.length==2){
                switch(commands[0]){
                    case "help":
                        switch(commands[1]){
                            case "nodes":
                                toret = "general information about nodes";
                                break;
                            case "commands":
                                toret = "general information about commands";
                                break;
                            case "command":
                                toret = "general information about a command";
                                break;
                            case "node":
                                toret = "general information about a node";
                                break;
                            case "capability":
                                toret = "displays uberdust capabilities";
                                break;
                        }
                        break;
                    case "list":
                        switch(commands[1]){
                            case "nodes":
                                toret = uberClient.listNodes();
                                break;
                            case "capabilities":
                                toret = uberClient.listCapabilities();
                                break;
                        }
                        break;
                    case "node":
                        break;
                }
            }
            else if(commands.length==1){
                switch (commands[0]){
                    case "help":
                        toret = "SkypeDust 1.01\n"+
                        "help: without arguments displays an intro message and displays a list with commands that help getting started."+
                        "help nodes : displays general information about nodes and commands that need node names as arguments \n"+
                        "help commands : displays general information about command\n"+
                        "list node : displays a list with the nodes available\n"+
                        "list commands : displays a list with the commands available\n"+
                        "help command : displays info about the command the user typed and a list of nodes that can be used with this command\n"+
                        "help node : displays info about the node the user typed and a list of commands that can be used\n"+
                        "node light : displays node light status\n"+
                        "node light turn_on : turns on the node's light\n"+
                        "node light turn_off : turns off the node's light\n"+
                        "node battery : displays node battery status\n"+
                        "node temperature : displays node temperature status\n"+
                        "node temperature set C : set temperature to celsius\n"+
                        "node co : displays node co status (co?)\n"+
                        "node noise : displays node noise status\n"+
                        "node noise turn_up : turn_up the volume\n"+
                        "node noise turn_down : turn_down the volume\n"+
                        "node battery : displays node battery status\n"+
                        "node parking : displays node parking status\n"+
                        "node parking available : sets the parking status to available\n"+
                        "node parking unavailable : sets the parking status to unavailable\n";
                        break;
                }
            }
            
            return toret;
        }
        else{
            return "No authorized user";        
        }
    }
}