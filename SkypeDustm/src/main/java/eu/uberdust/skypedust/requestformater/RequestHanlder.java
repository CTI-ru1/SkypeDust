/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.requestformater;

import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
/**
 *
 * @author gkatzioura
 */
public interface RequestHanlder {

    public void setAllowedContacts(String[] CommandCons);

    public void setUberClient(RestfullClient uberClient);
    
    public void setWebsocketClient(SkypedustWebSocket skypedustWebSocket);
    
    public String inputParse(String author,String body);

    public void setSkypeMessenger(SkypeMessenger skypeMessenger);
}
