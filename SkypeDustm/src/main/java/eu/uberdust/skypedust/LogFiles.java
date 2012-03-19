/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carnage
 */
public class LogFiles {
    
    public static String messages = "messages.log";
    
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    public static void writeLog(String path,String Log){
        
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
}
