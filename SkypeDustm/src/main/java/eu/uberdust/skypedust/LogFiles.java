/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class LogFiles {
    
    public static final String messages = "messages.log";
    public static final String logins = "logins.log";
    
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    public static void writeLoginLog(String username,int action) {
        
        String Log = "User: "+username+"\t"+"Action: ";
        
        if(action==0) Log = Log+"Logged in";
        else if(action==1) Log = Log+"Logged out";
        else Log = Log+"Failed attempt";
        
        writeLog(logins, Log);
    }
    
    public static void writeMessageLog(String contact,String message) {
        
        String Log = "Author: "+contact+"\t"+"Message: "+message;
        writeLog(messages, Log);
    }
    
    private static void writeLog(String path,String Log) {
        
        FileWriter fwrite = null;
        try {
            Date date = new Date();
            String dm = dateFormat.format(date);
            fwrite = new FileWriter(path, true);
            fwrite.write(dm+"\t"+Log+"\n");
            fwrite.close();
        } catch (IOException ex) {
            Logger.getLogger(LogFiles.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public static String[][] lastMessages(int numlines) {
        
        String[][] lastmessages = new String[numlines][3];
            
        try {
            
            FileInputStream finStream = new FileInputStream(messages);
            BufferedReader breader = new BufferedReader(new InputStreamReader(finStream));
         
            String strline;
            int count = 0;
            
            while((strline=breader.readLine())!=null) {
                            
                if(count<numlines-1) count++;
                else count =0;
                
                try{
                    String[] words = strline.split("\t");
                    lastmessages[count][0] = words[0];
                    lastmessages[count][1] = words[1].replace("Author: ","");
                    lastmessages[count][2] = words[2].replace("Message: ","");
                }
                catch (NullPointerException ex) {
                    Logger.getLogger(LogFiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        catch (IOException ex) {
            Logger.getLogger(LogFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lastmessages;
    }
}
