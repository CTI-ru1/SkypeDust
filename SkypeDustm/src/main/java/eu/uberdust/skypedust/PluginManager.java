/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.requestformater.DefaultRequest;
import eu.uberdust.skypedust.requestformater.RequestInterface;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author gkatzioura
 */
public class PluginManager {

    public static void AutoMode() {

    }
    
    public static void addrequestFormatter(String pluginpath){

        System.out.println("Unziping");
        unzip(pluginpath);
    }
    
    public static RequestInterface requestFormatter() {
    
        return new DefaultRequest();
    }
    
    private static void unzip(String zipath) {
      
        try {
            ZipFile zipFile = new ZipFile(zipath);
            String plugfolder = FileManage.PluginDir+"/"+new File(zipath).getName().replace(".zip", "");
            System.out.println("plugfolder "+plugfolder);
            new File(plugfolder).mkdir();
            Enumeration e = zipFile.entries();
            while(e.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry)e.nextElement();
                File destpath = new File(plugfolder,zipEntry.getName());
                destpath.getParentFile().mkdirs();
                if(zipEntry.isDirectory()) {
                    continue;
                }
                else {
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    int b;
                    byte buffer[] = new byte[1024];
                    FileOutputStream fout = new FileOutputStream(destpath);
                    BufferedOutputStream bout = new BufferedOutputStream(fout,1024);
                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bout.write(buffer, 0, b);
                    }
                    bout.flush();
                    bout.close();
                    bis.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
