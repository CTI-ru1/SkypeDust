/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplugin;

import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.requestformater.RequestHanlder;

/**
 *
 * @author carnage
 */

public class Aplugin implements RequestHanlder {

    @Override
    public void setAllowedContacts(String[] strings) {
    }

    @Override
    public void setUberClient(RestfullClient rc) {
    }

    @Override
    public void setWebsocketClient(SkypedustWebSocket sws) {
    }

    @Override
    public String inputParse(String string, String string1) {
        return "Default reply Message!";
    }

    @Override
    public void setSkypeMessenger(SkypeMessenger sm) {
    }

}