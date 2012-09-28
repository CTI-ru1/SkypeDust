/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.requestformater.CommandListener;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.pojos.NodeShortname;
import eu.uberdust.skypedust.ui.ConlistHandler;
import eu.uberdust.skypedust.useraccount.UserAccount;
import eu.uberdust.skypedust.requestformater.DefaultRequest;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author carnage
 */
public class SkypeDustManager {

    private UserAccount userAccount;
    private PluginManager pluginManager;
    private SkypeMessenger skypeMessenger;
    private DataProvider dataProvider;
    
    public SkypeDustManager() {
        
        userAccount = new UserAccount();
        
        userAccount.initSaccount();
        skypeMessenger = new SkypeMessenger(userAccount.getSession());
        
        CommandListener commandListener = new CommandListener();
        commandListener.setSkypeMessenger(skypeMessenger);
        dataProvider = new DataProvider();
        commandListener.setCommandCons(dataProvider.getAllowedContacts(userAccount.userSettings.getUsername()));
        commandListener.setUberClient(new RestfullClient("http://uberdust.cti.gr/rest/testbed/1"));
        commandListener.setWebsocketClient(SkypedustWebSocket.getInstance("ws://uberdust.cti.gr:80/readings.ws"));
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

        //userAccount.logoutAccount();
        userAccount.setAccount(username, nickname, password);
        dataProvider.insertAccount(username);
        //userAccount.initSaccount();
        
        /*
        userAccount.stopSaccount();
        userAccount = new UserAccount();
        userAccount.setAccount(username, nickname, password);
        userAccount.initSaccount();
        */
    }
    
    public void setAllowedContact(String contact) {
        dataProvider.insertAllowedContact(contact,userAccount.userSettings.getUsername());
    }
    
    public void removeAllowedContact(String contact) {
        if(dataProvider.removeAllowedContact(userAccount.userSettings.getUsername(), contact)) {
            System.out.println("Success");
        }
        else {
            System.out.println("Fail");
        }
    }
    
    public void setAllowedContacts(String[] allowedContacts) {
        userAccount.saveAllowedContacts(allowedContacts);
    }
    
    public String[] getAllowedContacts() {
        String[] contacts = dataProvider.getAllowedContacts(userAccount.userSettings.getUsername());
        return contacts;
    }
    
    public String[] getAccountContacts() {
        return userAccount.getAccountContacts();
    }
    
    public int insertUpdateNode(String realname,String nickname){
        
        return dataProvider.insertupdateNode(realname, nickname);
    }
    
    public List<NodeShortname> getnodesShortName(){
        return dataProvider.getnodesShortname();
    }
    
    public void deleteNode(String realname) {
        dataProvider.deleteNode(realname);
    }
}
