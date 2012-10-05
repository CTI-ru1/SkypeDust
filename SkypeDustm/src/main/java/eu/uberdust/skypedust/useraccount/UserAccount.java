package eu.uberdust.skypedust.useraccount;

import com.skype.api.Account;
import com.skype.api.Contact;
import com.skype.api.ContactGroup;
import eu.uberdust.skypedust.FileManage;
import eu.uberdust.skypedust.LogFiles;
import eu.uberdust.skypedust.appkeypair.AppKeyPairMgr;
import eu.uberdust.skypedust.connectivity.SkypedustWebSocket;
import eu.uberdust.skypedust.connectivity.VoipListener;
import eu.uberdust.skypedust.pojos.UserSettings;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import eu.uberdust.skypedust.util.MySession;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private static AppKeyPairMgr appKeyPairMgr;
    private static MySession mySession = new MySession();
    public static final String noContacts = "No Contacts";
    private static final String algo = "AES";
    private static final byte[] enckey = new byte[]{'C', 'h', 'a', 'n', 'g', 'e', 't','h', 'i', 's', 'o','n', 'e', '!', '!', '!' };
    
    FileManage fileManage = new FileManage();
    public UserSettings userSettings;
    public boolean LoggedIn = false;
    private XmlConfs xmlConfs;
    private RequestHanlder cmdListener;
    private VoipListener voipListener;
    private SkypedustWebSocket skypedustWebSocket;
    
    public UserAccount(){
            xmlConfs = new XmlConfs();
            userSettings = xmlConfs.readSettingsConf(fileManage.dpath+FileManage.SettingsFile);            
            userSettings.setPassword(getEncrypted());
    }
    
    public void setAccount(String username,String nickname,String password){

        System.out.println(username+"/"+nickname+"/"+password);
        xmlConfs.writeSettingsConf(fileManage.dpath+FileManage.SettingsFile, username, nickname,"nope");
        storeEncrypted(password);
        userSettings.setUsername(username);
        userSettings.setNickname(nickname);
        userSettings.setNickname(password);
    }
    
    public void registerAccount(String username,String fullname,String password,String email,String phonenum) throws UserException {
            
        try {
            Account account = mySession.mySkype.GetAccount(username);
            System.out.println("Account "+account.GetVerifiedEmail()+" "+account.GetSkypenameHash());
            account.SetStrProperty(Account.PROPERTY.fullname.getId(),fullname);
            account.Register(password, false, false, email, false);
            account.SetStrProperty(Account.PROPERTY.phone_home.getId(), phonenum);            
        }
        catch (NullPointerException ex){
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserException(UserException.AccountExists);
        }
    }
    
    public void addContact(String contactname,String message) {

        Contact contact = mySession.mySkype.GetContact(contactname);
        contact.SetBuddyStatus(true, true);
        contact.SendAuthRequest(message, Contact.EXTRA_AUTHREQ_FIELDS.SEND_VERIFIED_EMAIL.getId());
    }

    public void removeContact(String contactname) {
        
        mySession.mySkype.GetContact(contactname).SetBuddyStatus(false, false);
    }
    
    public void setRequestHandler(RequestHanlder requestHandler) throws UserException {
        try {
            this.cmdListener = requestHandler;
            mySession.myJavaTutorialListeners.setcommandListener(cmdListener);
        }
        catch (NullPointerException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserException(UserException.Uninitialized);
        }
    }
    
    /**
     *
     * @return
     */
    public RequestHanlder getRequestHandler() {
        return cmdListener;
    }
    
    public void setVoipListener(VoipListener voipListener) {
        this.voipListener = voipListener;
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
            
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                IOException | NoSuchAlgorithmException | NoSuchPaddingException  ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return password;
    }
    
    public boolean initSaccount(){
        
        try {
            startRuntime();
            setKeypair();
            sessionSignin();
            LogFiles.writeLoginLog(userSettings.getUsername(),0);
            return true;
        }
        catch (UserException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
            if(ex.getMessage().equals(UserException.WrongCredentials));
                LogFiles.writeLoginLog(userSettings.getUsername(),-1);
                return false;
        }
    }

    private void setKeypair() throws UserException {
     
        appKeyPairMgr = new AppKeyPairMgr();
        if(!appKeyPairMgr.setAppKeyPairFromFile())
            throw new UserException(UserException.KeypairProblem);
    }
        
    private void sessionSignin() throws UserException {
        
        try {
            
            System.out.println("User settings "+userSettings.getUsername()+" "+userSettings.getPassword());
            
            mySession.doCreateSession(null,userSettings.getUsername().toString(), appKeyPairMgr);
            

            if(!mySession.mySignInMgr.Login(null, mySession,userSettings.getPassword().toString())) {
                throw new UserException(UserException.WrongCredentials);
            }
            else {
                LoggedIn = true;
            }
        }
        catch(NullPointerException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public String[] getAllowedContacts(){
        
        ArrayList allowedContacts = xmlConfs.readuAllowedContacts(
                fileManage.dpath+userSettings.getUsername()+"_"+FileManage.AllowedContactsFile,
                userSettings.getUsername());

        if(allowedContacts!=null) {

            if(allowedContacts.isEmpty()){
                allowedContacts.add(noContacts);
            }
            
            String[] toallowedlist = new String[allowedContacts.size()];
            for(int i=0;i<allowedContacts.size();i++)
                toallowedlist[i]=allowedContacts.get(i).toString();
            
            return toallowedlist;
        
        }
        else {
            return new String[] {"No Contacts"};
        }
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
    
    public boolean stopSaccount(){
        
        if(mySession.isLoggedIn()) {
            mySession.mySignInMgr.Logout(null, mySession);
            mySession.doTearDownSession();
            LogFiles.writeLoginLog(userSettings.getUsername(),1);
            return true;
        }
        else {
            return false;
        }
    }
    
    public void saveAllowedContact(String contact) {
        
        List<String> contacts = new ArrayList<>(Arrays.asList(getAllowedContacts()));
        contacts.add(contact);
        saveAllowedContacts(contacts.toArray(new String[contacts.size()]));
    }
    
    public void removeAllowedContact(String contact) {
    
        List<String> contacts = new ArrayList<>(Arrays.asList(getAllowedContacts()));
        contacts.remove(contact);
        saveAllowedContacts(contacts.toArray(new String[contacts.size()]));
    }
    
    public void saveAllowedContacts(String[] allowedContacts){
        
        System.out.println(new Integer(allowedContacts.length).toString());
        xmlConfs.writeAllowedContacts(fileManage.dpath+userSettings.getUsername()+"_"+FileManage.AllowedContactsFile,
                allowedContacts,
                userSettings.getUsername());
    }
    
    public String[] getAccountContacts() throws UserException {

        try{
        
            Contact[] contacts = mySession.mySkype.GetHardwiredContactGroup(ContactGroup.TYPE.ALL_BUDDIES).GetContacts();

            String[] usernames = new String[contacts.length];
            for(int i=0;i<contacts.length;i++) {
                usernames[i] = contacts[i].GetIdentity();
            }

            return usernames;
        }
        catch(NullPointerException ex) {
            Logger.getLogger(UserAccount.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserException(UserException.Uninitialized);
        }
    }
    
    public MySession getSession() {
        return mySession;
    }
    
    public class UserException extends Exception{
    
        public static final String KeypairProblem = "Outdated Keypair or No keypair";
        public static final String RuntimeProblem = "Wrong Runtime Or Missing";
        public static final String WrongCredentials = "Wrong username or password";
        public static final String Uninitialized = "SypeDust not initialized properly";
        public static final String AccountExists = "Skype Account already exists";
        
        public UserException(String message){
            super(message);
        }
    }
}