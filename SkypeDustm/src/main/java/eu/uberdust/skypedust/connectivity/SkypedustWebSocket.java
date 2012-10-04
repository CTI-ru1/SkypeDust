/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.skypedust.DataProvider;
import eu.uberdust.skypedust.pojos.CapabilityNickname;
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
    private Map<String,List<String>> registeredUsers = new HashMap<>();
    
    public static SkypedustWebSocket getInstance(String webSocketread) {
        synchronized (SkypedustWebSocket.class) {
            if(instance == null) {
                instance = new SkypedustWebSocket(webSocketread);
            }
        }
        return instance;
    }
    
    private SkypedustWebSocket(String websocket) {
        
        WSReadingsClient.getInstance().setServerUrl(websocket);
        WSReadingsClient.getInstance().addObserver(this);
    }

    public void subscribeUpdate(String contact,String node,String capability) {
                        
        WSReadingsClient.getInstance().subscribe(getnodeName(node), getcapabilityName(capability));
        
        subscribeUser(getnodeName(node), getcapabilityName(capability), contact);
    }
    
    private void subscribeUser(String node,String capability,String contact) {
        
        DataProvider dataProvider = new DataProvider();
        dataProvider.userSubscribe(contact, node, capability);
        dataProvider.close();        
    }
    
    @Override
    public void update(Observable o, Object arg) {
        
        System.out.println("Observer called");
        
        if(!(o instanceof  WSReadingsClient)) {
            return;
        }
        
        System.out.println("Got Websocket Message");
        

        if( arg instanceof Message.NodeReadings) {
        
            System.out.println("Got node Reading");
            
            Message.NodeReadings reading =(Message.NodeReadings)arg;
        
            final String node = new String(reading.getReading(0).getNode());
            final String capability = new String(reading.getReading(0).getCapability());
            
            
            new Thread(new Runnable() {

                @Override
                public void run() {
                    
                    DataProvider dataProvider = new DataProvider();
                
                    String[] contacts = dataProvider.getSubscribedContacts(node, capability);
                    skypeMessenger.sendMessage(contacts,"Node "+ node+"Capability "+capability );
 
                    dataProvider.close();
                }
            }).start();
        }
    }
}
