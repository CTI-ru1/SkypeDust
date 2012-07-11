/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.requestformater.DefaultRequest;
import eu.uberdust.skypedust.requestformater.RequestInterface;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
    
    public static RequestInterface addrequestFormatter(String pluginpath){

        String toget = unzip(pluginpath);    
        Class class1 = getjarClass(toget,"SkypeDustPlugin");
        
        if(class1!=null) {
            try {
                RequestInterface reqInterface = (RequestInterface) class1.newInstance();
                System.out.println(reqInterface.uberRequest("",""));
                return reqInterface;
            } catch (InstantiationException ex) {
                Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
        
        return new DefaultRequest();
    }
    
    public static RequestInterface requestFormatter() {
    
        return new DefaultRequest();
    }
    
    private static Class getjarClass(String jarpath,String classname) {
    
        File file = new File(jarpath);
        
        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
            try {
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> entries = jarFile.entries();
                
                while(entries.hasMoreElements()) {
                    JarEntry element = entries.nextElement();
                    System.out.println(element.getName());
                    
                    if(element.getName().endsWith(".class")&&element.getName().contains(classname)) {
                        try {
                            Class c = classLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
                            return c;
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    private static String unzip(String zipath) {
      
        String myjar = null;
        
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
                    
                    if(zipEntry.getName().endsWith(".jar")) {
                        myjar = destpath.getAbsolutePath();
                    }
                        
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
        return myjar;
    }
    
}
