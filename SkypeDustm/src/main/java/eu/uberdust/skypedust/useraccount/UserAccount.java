/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.useraccount;

import com.skype.api.Contact;
import com.skype.api.ContactGroup.TYPE;
import com.skype.ipc.Transport;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ListModel;
import eu.uberdust.skypedust.FileManage;
import eu.uberdust.skypedust.LogFiles;
import eu.uberdust.skypedust.ui.SkypeDustApp;
import eu.uberdust.skypedust.appkeypair.AppKeyPairMgr;
import eu.uberdust.skypedust.connectivity.CommandListener;
import eu.uberdust.skypedust.connectivity.VoipListener;
import eu.uberdust.skypedust.util.MySession;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class UserAccount {

    public Hashtable userSettings;
    private static AppKeyPairMgr appKeyPairMgr = new AppKeyPairMgr();
    private static MySession mySession = new MySession();
    private AliveAccount aliveAccount;
    public static final String noContacts = "No Contacts";
    private static final String algo = "AES";
    private static final byte[] enckey = new byte[]{'C', 'h', 'a', 'n', 'g', 'e', 't','h', 'i', 's', 'o','n', 'e', '!', '!', '!' };
    
    FileManage fileManage = new FileManage();
    private XmlConfs xmlConfs;
    private CommandListener cmdListener;
    private VoipListener voipListener;
    
    public UserAccount(){
            xmlConfs = new XmlConfs();
            userSettings = xmlConfs.readSettingsConf(fileManage.dpath+fileManage.SettingsFile);            
            userSettings.put(XmlConfs.passwordtag,getEncrypted());
    }
    
    public void setAccount(String username,String nickname,String password){

        System.out.println(username+"/"+nickname+"/"+password);
        xmlConfs.writeSettingsConf(fileManage.dpath+fileManage.SettingsFile, username, nickname,"nope");
        storeEncrypted(password);
    }
    
    private void storeEncrypted(String password) {
        
        try {
            Key key = new SecretKeySpec(enckey,algo);
            Cipher c = Cipher.getInstance(algo);
            c.init(Cipher.ENCRYPT_MODE,key);
            byte[] encval = c.doFinal(password.getBytes());
            FileOutputStream fout = new FileOutputStream(FileManage.Passfile);
            PrintStream pstream = new PrintStream(fout);
            pstream.println(new BASE64Encoder().encode(encval));
            
        } catch (IOException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException |
                NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    private String getEncrypted() {
        
        String password = null;
        
        try {
            Key key = new SecretKeySpec(enckey,algo);
            Cipher c = Cipher.getInstance(algo);
            DataInputStream datains = new DataInputStream(new FileInputStream(FileManage.Passfile));
            BufferedReader breader = new BufferedReader(new InputStreamReader(datains));
            String encpass = breader.readLine();
            
            c.init(Cipher.DECRYPT_MODE,key);
            byte[] decordval = new BASE64Decoder().decodeBuffer(encpass);
            byte[] decVal = c.doFinal(decordval);
            
            password = new String(decVal);
            
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException | IOException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return password;
    }
    
    public void initSaccount(SkypeDustApp skypeDustView){
        
        aliveAccount = new AliveAccount(appKeyPairMgr,mySession,skypeDustView);
    }
    
    public void stopSaccount(){
        aliveAccount.aliveThread=false;
        mySession.mySignInMgr.Logout(null, mySession);
        LogFiles.writeLoginLog(userSettings.get("username").toString(),false);
    }
    
    public void saveAllowedContacts(String[] allowedContacts){
        
        System.out.println(new Integer(allowedContacts.length).toString());
        xmlConfs.writeAllowedContacts(fileManage.dpath+fileManage.AllowedContactsFile,
                                        allowedContacts,
                                        userSettings.get("username").toString());
    }
        
    /*calling this thread instead of freezing*/
    private class AliveAccount implements Runnable{

        public boolean aliveThread = true;
        private AppKeyPairMgr appKeyPairMgr;
        private MySession mySession;
        private SkypeDustApp skypeDustView;
        
        public AliveAccount(AppKeyPairMgr appKeyPairMgr,MySession mySession,SkypeDustApp skypeDustView){
        
            this.appKeyPairMgr = appKeyPairMgr;
            this.mySession = mySession;
            this.skypeDustView = skypeDustView;
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
        
        @Override
        public void run() {
        
            System.out.println("Running");
            startRuntime();
            if(appKeyPairMgr.setAppKeyPairFromFile()){    
                mySession.doCreateSession(null,userSettings.get("username").toString(), appKeyPairMgr);
                if(mySession.mySignInMgr.Login(null, mySession,userSettings.get("password").toString())){
                     
                    LogFiles.writeLoginLog(userSettings.get("username").toString(),true);
                    
                    //skypeDustView.changeStatusMessage("Logged in");
                    Contact[] contacts = mySession.mySkype.GetHardwiredContactGroup(TYPE.ALL_BUDDIES).GetContacts();
                    String[] constring = new String[contacts.length];
                    for(Integer i=0;i<contacts.length;i++)
                        constring[i]=contacts[i].GetStrProperty(Contact.PROPERTY.skypename);
                    skypeDustView.setContactList(constring);
                    skypeDustView.setAllowedList(getAllowedContacts());
                    cmdListener = new CommandListener(getAllowedContacts());
                    //voipListener = new VoipListener(mySession);
                    //voipListener.setAudio();
                    mySession.myJavaTutorialListeners.setcommandListener(cmdListener);
                    
                }
                else{
                    //skypeDustView.changeStatusMessage("Logged out: Wrong Settings");
                    //skypeDustView.showProbBox("LoginFail");
                }
            }
            
            while(aliveThread){
                try {
                    //System.out.println("Kai sleep");
                    Thread.sleep(6000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        private String[] getAllowedContacts(){
            
            ArrayList allowedContacts = xmlConfs.readuAllowedContacts(
                                        fileManage.dpath+fileManage.AllowedContactsFile,
                                        userSettings.get("username").toString());
            if(allowedContacts.size()==0){
                allowedContacts.add(noContacts);
            }
            
            String[] toallowedlist = new String[allowedContacts.size()];
            for(int i=0;i<allowedContacts.size();i++)
                toallowedlist[i]=allowedContacts.get(i).toString();
            
            return toallowedlist;
        }
        
        private void startRuntime(){
            try {
                String line;
                Process runtime = //Runtime.getRuntime().exec("/home/gkatzioura/programming/skype/linux-x86-skypekit-voicertp-novideo_3.5.1.719_465439/bin/linux-x86/linux-x86-skypekit-voicertp-novideo");
                //Process runtime = Runtime.getRuntime().exec("/home/gkatzioura/programming/skype/linux-x86-skypekit-voicertp-videortp_4.0.2.1529_806532/bin/linux-x86/linux-x86-skypekit-voicertp-videortp");
                        //exec("/home/gkatzioura/programming/skype/linux-x86-skypekit-voicepcm_4.0.2.1525_804635/bin/linux-x86/linux-x86-skypekit-voicepcm");
                        Runtime.getRuntime().exec("./skyperuntime/linux-x86-skypekit-voicertp-novideo");
                
                System.out.println("Done");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    };
}