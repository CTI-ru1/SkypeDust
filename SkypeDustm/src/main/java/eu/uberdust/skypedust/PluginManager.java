/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.pojos.PluginSettings;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class PluginManager {

    private static String settingsFile = "Settings.xml";
    
    public static void AddPlugin(String jarpath) {
        
        
        
        /*
        File file = new File(jarpath);
        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
            JarFile jarFile;

            try {
            
                jarFile = new JarFile(file);
                Enumeration<JarEntry> entries = jarFile.entries();
                
                while(entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if(jarEntry.getName().equals(settingsFile)) {
                        
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        try {
                            DocumentBuilder db = dbFactory.newDocumentBuilder();
                            try {
                                Document doc = db.parse(inputStream);
                                
                                System.out.println("Print");
                            } catch (SAXException ex) {
                                Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (ParserConfigurationException ex) {
                            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                        }                        
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
            
    private static PluginSettings getSettings(String jarpath) {
        return null;
    }
    
    /*

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

 public static final String requestag = "request";
    
    public static void AutoMode() {

    }
    
    public static PluginSettings addrequestFormatter(String pluginpath) throws PluginException{

        PluginSettings pluginSettings = new PluginSettings();
        unzip(pluginpath,pluginSettings);
        if(pluginSettings.getJarname()!=null) {

            readxmlSettings(pluginSettings);
            
            if(pluginSettings.getType().equals(requestag)) {
            
                Class class1 = getjarClass(pluginSettings.getJarname(),pluginSettings.getMainclass());
        
                if(class1!=null) {
                    return pluginSettings;
                    
                    //try {
                    //    RequestInterface reqInterface = (RequestInterface) class1.newInstance();
                    //    System.out.println(reqInterface.uberRequest("",""));
                    //    //return reqInterface;
                    //    return pluginSettings;
                    //} catch (InstantiationException ex) {
                    //    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                    //} catch (IllegalAccessException ex) {
                    //    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                    //}
                }
            
            } else {
                throw new PluginException(PluginException.exformatter);
            }
        } else {
            throw  new PluginException(PluginException.nojar);
        }
        return null;
    }
    
    public static RequestHanlder selectFormatter(String path) throws PluginException {
    
        PluginSettings pluginSettings = new PluginSettings();
        pluginSettings.setPath(path);
        String jarname = new File(pluginSettings.getPath()).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("jar");               
            }
        })[0].getName();
        
        readxmlSettings(pluginSettings);
        
        pluginSettings.setJarname(jarname);
        
        if(pluginSettings.getType().equals(requestag)) {
            
            Class class1 = getjarClass(pluginSettings.getJarname(),pluginSettings.getMainclass());
        
            if(class1!=null) {
                try {
                    RequestHanlder reqInterface = (RequestHanlder) class1.newInstance();
                    //System.out.println(reqInterface.uberRequest("",""));
                    return reqInterface;
                } catch (InstantiationException ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new PluginException(PluginException.notproper);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new PluginException(PluginException.notproper);
                }
            } else throw new PluginException(PluginException.notproper);
        } else throw new PluginException(PluginException.exformatter);   
    }
    
    private static void unzip(String zipath,PluginSettings pluginSettings) throws PluginException {
      
        String myjar = null;
        
        try {
            ZipFile zipFile = new ZipFile(zipath);
            String plugfolder = FileManage.PluginDir+"/"+new File(zipath).getName().replace(".zip", "");
            pluginSettings.setPath(plugfolder);
            System.out.println("plugfolder "+plugfolder);
            if(!new File(plugfolder).isDirectory()) {
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
            }
            else {
                throw new PluginException(PluginException.installed);
            }
        } catch (IOException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        pluginSettings.setJarname(myjar);
    }

    private static void readxmlSettings(PluginSettings pluginSettings) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new File(pluginSettings.getPath()+"/settings.xml"));
            doc.getDocumentElement().normalize();
        
            pluginSettings.setType(doc.getElementsByTagName("type").item(0).getTextContent());
            pluginSettings.setMainclass(doc.getElementsByTagName("mainclass").item(0).getTextContent());
        } catch (SAXException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    */
}
