package eu.uberdust.skypedust.requestformater;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.protobuf.Message.NodeReadings;
import eu.uberdust.skypedust.DataProvider;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;

/**
 *
 * @author Gkatzioura Emmanouil (gkatzioura)
 */
public class DefaultNotification implements NotificationHandler{

    private Message.NodeReadings nodeReadings;
    private SkypeMessenger skypeMessenger;
    
    @Override
    public void setNodeReadings(NodeReadings nodeReadings) {
        this.nodeReadings = nodeReadings;
    }
    
    @Override
    public void setSkypeMessenger(SkypeMessenger skypeMessenger) {
    
        this.skypeMessenger = skypeMessenger;
    }    
    
    @Override
    public void run() {
        
        System.out.println("Default notification");
        DataProvider dataProvider = new DataProvider();
        String[] contacts = dataProvider.getSubscribedContacts(
                nodeReadings.getReading(0).getNode(),
                nodeReadings.getReading(0).getCapability());
        
        skypeMessenger.sendMessage(contacts, "Received an automated message");
    }

    
}
