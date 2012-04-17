/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.useraccount;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import eu.uberdust.skypedust.FileManage;
import eu.uberdust.skypedust.LogFiles;
import eu.uberdust.skypedust.appkeypair.AppKeyPairMgr;
import eu.uberdust.skypedust.connectivity.CommandListener;
import eu.uberdust.skypedust.connectivity.VoipListener;
import eu.uberdust.skypedust.ui.SkypeDustApp;
import eu.uberdust.skypedust.util.MySession;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    public static final String noContacts = "No Contacts";
    private static final String algo = "AES";
    private static final byte[] enckey = new byte[]{'C', 'h', 'a', 'n', 'g', 'e', 't','h', 'i', 's', 'o','n', 'e', '!', '!', '!' };
    
    FileManage fileManage = new FileManage();
    private XmlConfs xmlConfs;
    private CommandListener cmdListener;
    private VoipListener voipListener;
    
    public UserAccount(){
            xmlConfs = new XmlConfs();
            userSettings = xmlConfs.readSettingsConf(fileManage.dpath+FileManage.SettingsFile);            
            userSettings.put(XmlConfs.passwordtag,getEncrypted());
    }
    
    public void setAccount(String username,String nickname,String password){

        System.out.println(username+"/"+nickname+"/"+password);
        xmlConfs.writeSettingsConf(fileManage.dpath+FileManage.SettingsFile, username, nickname,"nope");
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
        
        try {
            startRuntime();
            setKeypair();
            sessionSignin();
            LogFiles.writeLoginLog(userSettings.get("username").toString(),0);
        }
        catch (UserException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
            if(ex.getMessage().equals(UserException.WrongCredentials));
                LogFiles.writeLoginLog(userSettings.get("username").toString(),-1);
        }
    }

    private void setKeypair() throws UserException {
     
        if(!appKeyPairMgr.setAppKeyPairFromFile())
            throw new UserException(UserException.KeypairProblem);
    }
        
    private void sessionSignin() throws UserException {
        
        mySession.doCreateSession(null,userSettings.get("username").toString(), appKeyPairMgr);
        if(!mySession.mySignInMgr.Login(null, mySession,userSettings.get("password").toString()))
            throw new UserException(UserException.WrongCredentials);
    }    

    public void startListener() {
    
        cmdListener = new CommandListener(getAllowedContacts());
        mySession.myJavaTutorialListeners.setcommandListener(cmdListener);
    }
    
    public String[] getAllowedContacts(){
            
        ArrayList allowedContacts = xmlConfs.readuAllowedContacts(
                fileManage.dpath+FileManage.AllowedContactsFile,
                userSettings.get("username").toString());
        if(allowedContacts.isEmpty()){
            allowedContacts.add(noContacts);
        }
            
        String[] toallowedlist = new String[allowedContacts.size()];
        for(int i=0;i<allowedContacts.size();i++)
            toallowedlist[i]=allowedContacts.get(i).toString();
            
        return toallowedlist;
    }
    
    private void startRuntime() throws UserException {
        
        try {
            String line;
            Process runtime = //Runtime.getRuntime().exec("/home/gkatzioura/programming/skype/linux-x86-skypekit-voicertp-novideo_3.5.1.719_465439/bin/linux-x86/linux-x86-skypekit-voicertp-novideo");
                //Process runtime = Runtime.getRuntime().exec("/home/gkatzioura/programming/skype/linux-x86-skypekit-voicertp-videortp_4.0.2.1529_806532/bin/linux-x86/linux-x86-skypekit-voicertp-videortp");
                        //exec("/home/gkatzioura/programming/skype/linux-x86-skypekit-voicepcm_4.0.2.1525_804635/bin/linux-x86/linux-x86-skypekit-voicepcm");
                        Runtime.getRuntime().exec("./skyperuntime/linux-x86-skypekit-voicertp-novideo");
        } catch (IOException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserException(UserException.RuntimeProblem);
        }
    }
    
    public void stopSaccount(){
        
        mySession.mySignInMgr.Logout(null, mySession);
        LogFiles.writeLoginLog(userSettings.get("username").toString(),1);
    }
    
    public void saveAllowedContacts(String[] allowedContacts){
        
        System.out.println(new Integer(allowedContacts.length).toString());
        xmlConfs.writeAllowedContacts(fileManage.dpath+FileManage.AllowedContactsFile,
                allowedContacts,
                userSettings.get("username").toString());
    }    
}