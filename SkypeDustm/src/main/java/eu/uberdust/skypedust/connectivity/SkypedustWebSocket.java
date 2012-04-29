/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gkatzioura Emmanouil (gkatzioura)
 */
public class SkypedustWebSocket {

    private static SkypedustWebSocket instance = null;
    private static final String webSocketread = "ws://carrot.cti.gr:8080/uberdust/readings.ws";
    
    public static SkypedustWebSocket getInstance() {
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
    
    public static void setUpdate(String node,String capability) {
        
        Message.NodeReadings.Reading reading = Message.NodeReadings.Reading.newBuilder()
                .setNode("urn:test:0x1")
                .setCapability("light")
                .setTimestamp(new Date().getTime())
                .setDoubleReading(1)
                .build();
        Message.NodeReadings readings = Message.NodeReadings.newBuilder()
                .addReading(reading)
                .build();
        try {
            WSReadingsClient.getInstance().sendNodeReading(readings);
        } catch (IOException ex) {
            Logger.getLogger(SkypedustWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getUpdate(String node,String capability) {
        
        System.out.println("Setting Observer");
        
        final String mynode = node;
        final String mycapability = capability;
        
        Thread temp = new Thread(new Runnable() {
            @Override
            public void run() {
                WSReadingsClient.getInstance().subscribe(mynode,mycapability);
                WSReadingsClient.getInstance().addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        System.out.println("Get Message from web socket");
                        System.out.println("E kale Message: "+(Message.NodeReadings)arg);
                    }
                });
            }
        });
        temp.setDaemon(true);
        temp.start();
    }
}
