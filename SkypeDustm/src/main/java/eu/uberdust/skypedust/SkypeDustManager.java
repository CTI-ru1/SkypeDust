/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.pojos.CapabilityNickname;
import eu.uberdust.skypedust.pojos.NodeNickname;
import eu.uberdust.skypedust.requestformater.CommandListener;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import eu.uberdust.skypedust.useraccount.UserAccount;
import eu.uberdust.skypedust.useraccount.UserException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carnage
 */
public class SkypeDustManager {

    private UserAccount userAccount;
    private PluginManager pluginManager;
    private SkypeMessenger skypeMessenger;
    private DataProvider dataProvider;
    private RequestHanlder requestHanlder;
    
    public SkypeDustManager() {
        
        userAccount = new UserAccount();
        dataProvider = new DataProvider();
        
        if(userAccount.initSaccount()) {
            
            skypeMessenger = new SkypeMessenger(userAccount.getSession());
        
            requestHanlder = new CommandListener();
            requestHanlder.setSkypeMessenger(skypeMessenger);
            
            requestHanlder.setAllowedContacts(dataProvider.getAllowedContacts(userAccount.userSettings.getUsername()));
            requestHanlder.setUberClient(new RestfullClient("http://uberdust.cti.gr/rest/testbed/1"));
            requestHanlder.setWebsocketClient(SkypedustWebSocket.getInstance("ws://uberdust.cti.gr:80/readings.ws"));
            
            //requestHanlder.setAllowedContacts(userAccount.getAllowedContacts());
            
            try {
                userAccount.setRequestHandler(requestHanlder);
            } catch (UserException ex) {
                Logger.getLogger(SkypeDustManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            System.out.println("Not logged in");
        }
        
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
    
    private void refreshContacts() {
        
        userAccount.getRequestHandler().setAllowedContacts(
                dataProvider.getAllowedContacts(
                userAccount.userSettings.getUsername()));
    }
    
    public void setAllowedContact(String contact) {
        dataProvider.insertAllowedContact(contact,userAccount.userSettings.getUsername());
        refreshContacts();
    }
    
    public void removeAllowedContact(String contact) {
        if(dataProvider.removeAllowedContact(userAccount.userSettings.getUsername(), contact)) {
            System.out.println("Success");
            refreshContacts();
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
        
        try {
            return userAccount.getAccountContacts();
        } catch (UserException ex) {
            Logger.getLogger(SkypeDustManager.class.getName()).log(Level.SEVERE, null, ex);
            return new String[] {"No Contacts"};
        }
    }
    
    public int insertUpdateNode(String realname,String nickname){
        
        return dataProvider.insertupdateNode(realname, nickname);
    }
    
    public List<NodeNickname> getnodesShortName(){
        return dataProvider.getnodesShortname();
    }
    
    public void deleteNode(String realname) {
        dataProvider.deleteNode(realname);
    }
    
    public int insertUpdateCapability(String realname,String nickname) {
        return dataProvider.insertupdateCapability(realname, nickname);
    }
    
    public List<CapabilityNickname> getcapablityShortName() {
        return dataProvider.getcapabilitiesNickname();
    }
    
    public void deleteCapability(String realname) {
        dataProvider.deleteCapability(realname);
    }
    
    public void insertPlugin(String path) {
        pluginManager.AddPlugin(path);
    }
}
