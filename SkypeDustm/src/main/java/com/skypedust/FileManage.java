/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skypedust;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author carnage
 */
public class FileManage {

    public static final String SettingsFile = "Settings.xml";
    public static final String AllowedContactsFile = "AllowedContacts.xml";
    public static final String ConfigurationDir = "SkypeDustConf";
    public static final String LogFilesDir = "LogFiles";
    public static final String Logaction = "logged.log";
    public static final String CommandsLog = "commands.log";
    
    public File confdir;
    public File logfdir;
    public String dpath;
    private static final String ALGO = "AES";
        
    private Cipher eCipher;
    private Cipher dCipher;
    private static final String encryptKey = "I like wookies";
    static final String HEXES = "0123456789ABCDEF";

    
    public FileManage(){
        
        confdir = new File(ConfigurationDir);
        if(!this.confdir.exists()) this.confdir.mkdir();
        dpath = this.confdir.getPath()+getOsSlash();
        createFile(this.confdir,SettingsFile);
        createFile(this.confdir,AllowedContactsFile);
        logfdir = new File(LogFilesDir);
        createFile(logfdir,Logaction);
        createFile(logfdir,CommandsLog);
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