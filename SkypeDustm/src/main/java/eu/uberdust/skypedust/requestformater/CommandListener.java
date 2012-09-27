/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.requestformater;

import com.skype.api.Sms;
import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import eu.uberdust.skypedust.requestformater.DefaultRequest;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class CommandListener implements RequestHanlder {

    private ArrayList<String> commandcons;
    private RestfullClient uberClient;
    private SkypedustWebSocket skypedustWebSocket;
    private RequestHanlder skypedustRequest;
    private SkypeMessenger skypeMessenger;

    public void setCommandCons(String[] contacts) {
        
        commandcons = new ArrayList<>();
        commandcons.addAll(Arrays.asList(contacts));
        //skypedustRequest.setCommandCons(commandcons);
    }
    
    public void setSkypedustRequest(RequestHanlder skypedustRequest1){
        
        skypedustRequest = skypedustRequest1;
    }
    
    public void setCommandCons(ArrayList<String> CommandCons) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setUberClient(RestfullClient uberClient) {
        this.uberClient = uberClient;
        this.uberClient.setSkypeMessenger(skypeMessenger);
    }

    public void setWebsocketClient(SkypedustWebSocket skypedustWebSocket) {
        this.skypedustWebSocket = skypedustWebSocket;
        this.skypedustWebSocket.setSkypeMessenger(skypeMessenger);
    }

    public String inputParse(String author, String body) {
        if(commandcons.contains(author))
        {
            String toret = "Unregognized command please type help to see your options";            
            String[] commands = body.split(" ");
            if(commands.length==4){
            
                if(commands[0].equals("node")) {

                    toret = uberClient.setTonodecapability(commands[1],commands[2],commands[3]);
                }
                
                if(commands[0].equals("websocket")&commands[1].equals("node")) {
                    skypedustWebSocket.subscribeUpdate(author,commands[2],commands[3]);
                }

            }
            else if(commands.length==3){

                if("node".equals(commands[0])){
                    switch(commands[2]){
                        case "capabilities":
                            toret = uberClient.getnodeCapabilities(commands[1]);
                            break;
                        default:
                            toret = uberClient.getnodeCapability(commands[1],commands[2],1);
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
                }
            }
            else if(commands.length==1){
                switch (commands[0]){
                    case "websocket":
                        System.out.println("testing web socket");
                        skypeMessenger.sendMessage(new String[] {author},"sdasdadas");
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

    @Override
    public void setSkypeMessenger(SkypeMessenger skypeMessenger) {
        this.skypeMessenger = skypeMessenger;
    }
}