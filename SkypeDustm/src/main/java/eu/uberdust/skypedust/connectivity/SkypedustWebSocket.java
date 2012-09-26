/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gkatzioura Emmanouil (gkatzioura)
 */
public class SkypedustWebSocket extends UberdustClient implements Observer {

    private static SkypedustWebSocket instance = null;
    //private static final String webSocketread = "ws://carrot.cti.gr:8080/uberdust/readings.ws";
    
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

    public void subscribeUpdate(String node,String capability) {
        WSReadingsClient.getInstance().subscribe(node, capability);
        WSReadingsClient.getInstance().addObserver(this);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof  WSReadingsClient)) {
            return;
        }
        
        if( arg instanceof Message.NodeReadings) {
            Message.NodeReadings reading =(Message.NodeReadings)arg;
            System.out.println("websocket: "+reading.getReading(0).getNode());
        }
    }
}
