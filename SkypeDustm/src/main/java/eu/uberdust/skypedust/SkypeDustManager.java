package eu.uberdust.skypedust;

import eu.uberdust.skypedust.PluginManager.PluginException;
import eu.uberdust.skypedust.connectivity.RestfullClient;
import eu.uberdust.skypedust.connectivity.SkypeMessenger;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.pojos.CapabilityNickname;
import eu.uberdust.skypedust.pojos.NodeNickname;
import eu.uberdust.skypedust.pojos.PluginSettings;
import eu.uberdust.skypedust.requestformater.CommandListener;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import eu.uberdust.skypedust.useraccount.UserAccount;
import eu.uberdust.skypedust.useraccount.UserException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gkatzioura Emmanouil (gkatzioura)
 */
public class SkypeDustManager {

    private UserAccount userAccount;
    private SkypeMessenger skypeMessenger;
    private DataProvider dataProvider;
    private RequestHanlder requestHanlder;
    
    public SkypeDustManager() {
        
        userAccount = new UserAccount();
        dataProvider = new DataProvider();
        
        if(userAccount.initSaccount()) {
            
            skypeMessenger = new SkypeMessenger(userAccount.getSession());
        
            requestHanlder = getRequestHandler();
            
            try {
                userAccount.setRequestHandler(requestHanlder);
            } catch (UserException ex) {
                Logger.getLogger(SkypeDustManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            System.out.println("Not logged in");
        }
        
        /*
        dataProvider.userSubscribe("okas", "dksk", "lala");
        dataProvider.getSubsribed();
        System.out.println("Unsubscribing");
        dataProvider.userUnsubscribe("asd", "dksk", "lala");
        */
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
    
    public String[] insertPlugin(String path) {
    
        try {
            PluginSettings  pluginSettings = PluginManager.AddPlugin(path);
            dataProvider.addPlugin(pluginSettings.getName(), pluginSettings.getType(), pluginSettings.getPath());
            
            return new String[] {pluginSettings.getName(),pluginSettings.getType(),pluginSettings.getPath(),"Disabled"};
        } catch (PluginException ex) {
            Logger.getLogger(SkypeDustManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Plugin Exception");
        }
        
        return null;
    }
    
    public String[][] getPlugins() {
        
        String[][] plugins = dataProvider.getPlugins();
        
        return plugins;
    }
    
    public boolean removePlugin(String name,String path) {
    
        if(PluginManager.RemovePlugin(path)) {
        
            dataProvider.removePlugin(name);
            return true;
        }
        else {
            return false;
        }
    }
    
    public String[] enabledisPlugin(String name) {
        
        dataProvider.getenabledPluginpath(PluginManager.requesthandlertype);
        
        String[] plugin = dataProvider.getPlugin(name);
        
        if(plugin[3].equals("Enabled")) {
            
            dataProvider.enabledisPlugin(name, Boolean.FALSE);               
        }
        else {
            
            dataProvider.enabledisPlugin(name, Boolean.TRUE);
        }
        
        if(plugin[1].equals(PluginManager.requesthandlertype)) {
            requestHanlder = getRequestHandler();
            try {
                userAccount.setRequestHandler(requestHanlder);
            } catch (UserException ex) {
                Logger.getLogger(SkypeDustManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return dataProvider.getPlugin(name);        
    }
    
    public RequestHanlder getRequestHandler() {
        
        RequestHanlder reqHanlder = new CommandListener();
        
        String pluginPath = dataProvider.getenabledPluginpath(PluginManager.requesthandlertype);
        
        if(pluginPath!=null) {
            try {
                
                reqHanlder = PluginManager.getRequestHanlderPlugin(pluginPath);
            } catch (PluginException ex) {
                Logger.getLogger(SkypeDustManager.class.getName()).log(Level.SEVERE, null, ex);
                dataProvider.enabledisPlugin(pluginPath, Boolean.FALSE);
            }
        }
        
        reqHanlder.setSkypeMessenger(skypeMessenger);            
        reqHanlder.setAllowedContacts(dataProvider.getAllowedContacts(userAccount.userSettings.getUsername()));
        reqHanlder.setUberClient(new RestfullClient("http://uberdust.cti.gr/rest/testbed/1"));
        reqHanlder.setWebsocketClient(SkypedustWebSocket.getInstance("ws://uberdust.cti.gr:80/readings.ws"));

        return reqHanlder;
    }
    
    public void onExit() {
        dataProvider.close();
    }
}
