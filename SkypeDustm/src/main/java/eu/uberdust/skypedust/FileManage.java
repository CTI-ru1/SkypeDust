package eu.uberdust.skypedust;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class FileManage {

    public static final String SettingsFile = "Settings.xml";
    public static final String AllowedContactsFile = "AllowedContacts.xml";
    public static final String ConfigurationDir = "SkypeDustConf";
    public static final String ServerConf = "Server.xml";
    public static final String LogFilesDir = "LogFiles";
    public static final String Passfile = ".passwd";
    public static final String PluginDir = "plugins";
    
    public File confdir;
    public File logfdir;
    public File plugdir;
    public String dpath;
    private static final String ALGO = "AES";
        
    private Cipher eCipher;
    private Cipher dCipher;
            
    public FileManage(){
        
        plugdir = new File(PluginDir);
        if(!this.plugdir.exists()) this.plugdir.mkdir();
        confdir = new File(ConfigurationDir);
        if(!this.confdir.exists()) this.confdir.mkdir();
        dpath = this.confdir.getPath()+getOsSlash();
        createFile(this.confdir,SettingsFile);
        createFile(this.confdir,AllowedContactsFile);
        logfdir = new File(LogFilesDir);
    }
    
    public String getOsSlash(){
        
        if(System.getProperty("os.name").indexOf("Windows")==-1) return "/";
        else return "\\";
    }

    private int createFile(File directory,String filename){
        
        File nfile = new File(directory.getPath()+getOsSlash()+filename);
        if(!nfile.exists()){
            try{
                nfile.createNewFile();
                return 1;
            }
            catch(IOException ex){
                Logger.getLogger(FileManage.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
        else{
            return 0;
        }
    }

}