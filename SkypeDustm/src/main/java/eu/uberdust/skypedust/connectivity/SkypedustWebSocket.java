/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 *
 * @author Gkatzioura Emmanouil (gkatzioura)
 */
public class SkypedustWebSocket extends UberdustClient implements Observer {

    private static SkypedustWebSocket instance = null;

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
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof  WSReadingsClient)) {
            return;
        }
        
        System.out.println("Got Websocket Message");
        
        if( arg instanceof Message.NodeReadings) {
            Message.NodeReadings reading =(Message.NodeReadings)arg;
            
            String key = reading.getReading(0).getNode()+"@"+reading.getReading(0).getCapability();
            System.out.println(key);
            
            skypeMessenger.sendMessage(new String[] {"mangkatz"}, key);
        }
    }
}
