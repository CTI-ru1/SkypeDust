/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.requestformater;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;

/**
 *
 * @author carnage
 */
public interface NotificationHandler extends Runnable {
    
    public void setNodeReadings(Message.NodeReadings nodeReadings);
    
    public void setSkypeMessenger(SkypeMessenger skypeMessenger);
}
