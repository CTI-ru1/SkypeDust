/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import edu.emory.mathcs.backport.java.util.Arrays;
import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.skypedust.DataProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Gkatzioura Emmanouil (gkatzioura)
 */
public class SkypedustWebSocket extends UberdustClient implements Observer {

    private static SkypedustWebSocket instance = null;
    private Map<String,List<String>> registeredUsers = new HashMap<String,List<String>>();
    
    public static SkypedustWebSocket getInstance(String webSocketread) {
        synchronized (SkypedustWebSocket.class) {
            if(instance == null) {
                instance = new SkypedustWebSocket();
                WSReadingsClient.getInstance().setServerUrl(webSocketread);
            }
        }
        return instance;
    }
    
    private SkypedustWebSocket() {
    }

    public void subscribeUpdate(String contact,String node,String capability) {
                        
        WSReadingsClient.getInstance().subscribe(node, getcapabilityName(capability));
        WSReadingsClient.getInstance().addObserver(this);
        //subscribeUser(node+"@"+capability,contact);
        subscribeUser(node, capability, contact);
    }
    
    private void subscribeUser(String node,String capability,String contact) {
        
        /*
        if(registeredUsers.containsKey(id)) {
            
            if(!registeredUsers.get(id).contains(contact)) {
                registeredUsers.get(id).add(contact);
            }
        }
        else {
            registeredUsers.put(id, Arrays.asList(new String[] {contact}));
        }*/
    }
    
    private void unsubscribeUser(String node,String capability) {
        
        
    }
    
    private String[] subscribedUsers(String node,String capability) {
        
        /*
        if(registeredUsers.containsKey(id)) {
        
            return registeredUsers.get(id).toArray(new String[registeredUsers.get(id).size()]);
        }
        else {
            return null;
        }*/
        
        return null;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof  WSReadingsClient)) {
            return;
        }
        
        System.out.println("Got Websocket Message");
        

        if( arg instanceof Message.NodeReadings) {
            Message.NodeReadings reading =(Message.NodeReadings)arg;
        
            DataProvider dataProvider = new DataProvider();
            String[] contacts = dataProvider.getAllowedContacts("gkatzbot");
            
            
            skypeMessenger.sendMessage(contacts,"Node "+
                    reading.getReading(0).getNode()+
                    " reading "+
                    reading.getReading(0).getStringReading());
            /*
            String id = reading.getReading(0).getNode()+"@"+reading.getReading(0).getCapability();
            String[] users = getUsers(id);
            
            if(users!=null) {
                skypeMessenger.sendMessage(users, "lalalal");
            }
            else {
                skypeMessenger.sendMessage(users, "What the fuck");
            }*/            
        }
    }
}
