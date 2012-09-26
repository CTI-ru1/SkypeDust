/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.connectivity.CommandListener;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.ui.ConlistHandler;
import eu.uberdust.skypedust.useraccount.UserAccount;
import eu.uberdust.skypedust.requestformater.DefaultRequest;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author carnage
 */
public class SkypeDustManager {

    private UserAccount userAccount;
    private PluginManager pluginManager;
    private SkypeMessenger skypeMessenger;
    
    public SkypeDustManager() {
        
        
        userAccount = new UserAccount();
        userAccount.initSaccount();
        
        skypeMessenger = new SkypeMessenger(userAccount.getSession());
        
        CommandListener commandListener = new CommandListener();
        commandListener.setSkypeMessenger(skypeMessenger);
        commandListener.setCommandCons(userAccount.getAllowedContacts());
        commandListener.setUberClient(new RestfullClient("http://uberdust.cti.gr/rest/testbed/1"));
        commandListener.setWebsocketClient(SkypedustWebSocket.getInstance("ws://uberdust.cti.gr/readings.ws"));
        commandListener.setCommandCons(userAccount.getAllowedContacts());
        userAccount.setCommandListener(commandListener);
        
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setUserAccount(String username,String nickname,String password) {

        userAccount.stopSaccount();
        userAccount = new UserAccount();
        userAccount.setAccount(username, nickname, password);
        userAccount.initSaccount();
        //userAccount.startListener();
    }
    
    public void setAllowedContacts(String[] allowedContacts) {
        userAccount.saveAllowedContacts(allowedContacts);
    }
}
