/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.pojos.PluginSettings;
import eu.uberdust.skypedust.pojos.RequestHandlerSettings;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class PluginManager {

    private static String settingsFile = "Settings.xml";
    public static String requesthandlertype = "requesthandler";
    private static String requesthandlerclass = "requesthandlerclass";
    private static String websockethanlderclass = "websockethanlderclass";
    
    public static PluginSettings AddPlugin(String jarpath) throws PluginException {
        
        PluginSettings pluginSettings;
        
        try {
            
            pluginSettings = getSettings(jarpath);
            System.out.println(pluginSettings.getName());
            File first = new File(jarpath);
            File second = new File(FileManage.PluginDir+"/"+first.getName());
            pluginSettings.setPath(second.getAbsolutePath());
            FileUtils.copyFile(first,second);
            
            return pluginSettings;
        } catch (IOException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new PluginException(PluginException.wrongstructure);
        } catch (SAXException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new PluginException(PluginException.malformedxml);
        } catch (NullPointerException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new PluginException(PluginException.noxml);
        }                
    }
    
    public static boolean RemovePlugin(String plugin) {
        
        File f = new File(plugin);
        return f.delete();
    }
    
    public static RequestHanlder getRequestHanlderPlugin(String jarpath) throws PluginException {
        
        try {
            
            RequestHandlerSettings requestSettings = getRequestHandlerSettings(jarpath);
            File file = new File(jarpath);
            
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
            JarFile jarFile = new JarFile(file);
            JarEntry jarEntry = jarFile.getJarEntry(requestSettings.getRequesthandlerclass()+".class");
            System.out.println(jarEntry.getName());
            
            try {
                Class requestclass = classLoader.loadClass(jarEntry.getName().replaceAll(".class","").replaceAll("/", "."));
                try {
                    RequestHanlder requestHanlder = (RequestHanlder) requestclass.newInstance();
                    return requestHanlder;
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new PluginException(PluginException.problemloading);
                }               
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new PluginException(PluginException.requestclassmissing);
            }                        
        } catch (IOException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new PluginException(PluginException.wrongstructure);
        } catch (SAXException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new PluginException(PluginException.malformedxml);
        } catch (NullPointerException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new PluginException(PluginException.noxml);
        }
    }

    private static PluginSettings getSettings(String jarpath) throws SAXException, PluginException, IOException {
        
        File file = new File(jarpath);
        if(!file.getName().toLowerCase().endsWith(".jar")) {
            throw new PluginException(PluginException.nojar);
        }
        
        JarFile jarFile = new JarFile(file);
        JarEntry jarEntry = jarFile.getJarEntry(settingsFile);
        InputStream inputStream = jarFile.getInputStream(jarEntry);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            
            PluginSettings pluginSettings = new PluginSettings();
            Document doc = documentBuilder.parse(inputStream);
                
            pluginSettings.setName(doc.getElementsByTagName("name").item(0).getTextContent());
            pluginSettings.setAuthor(doc.getElementsByTagName("author").item(0).getTextContent());
            pluginSettings.setEmail(doc.getElementsByTagName("email").item(0).getTextContent());
            pluginSettings.setUrl(doc.getElementsByTagName("url").item(0).getTextContent());
            pluginSettings.setLicense(doc.getElementsByTagName("license").item(0).getTextContent());
            pluginSettings.setVersion(doc.getElementsByTagName("version").item(0).getTextContent());
            pluginSettings.setDescription(doc.getElementsByTagName("description").item(0).getTextContent());
            pluginSettings.setType(doc.getElementsByTagName("type").item(0).getTextContent());
                
            return pluginSettings;
                
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }                                       
        
        
        return null;
    }

    private static RequestHandlerSettings getRequestHandlerSettings(String jarpath) throws IOException, SAXException {

        File file = new File(jarpath);
        JarFile jarFile = new JarFile(file);
        JarEntry jarEntry = jarFile.getJarEntry(settingsFile);
        InputStream inputStream = jarFile.getInputStream(jarEntry);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            RequestHandlerSettings requestHandlerSettings = new RequestHandlerSettings();
            Document document = documentBuilder.parse(inputStream);
            
            requestHandlerSettings.setRequesthandlerclass(document.getElementsByTagName(requesthandlerclass).item(0).getTextContent());
            //requestHandlerSettings.setWebsocketHanlderclass(document.getElementsByTagName(websockethanlderclass).item(0).getTextContent());
            
            return requestHandlerSettings;
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;   
    }    
    
    public static class PluginException extends Exception {
        
        public static final String nojar = "Not a jar File.";
        public static final String noxml = "No xml file found on root directory.";
        public static final String malformedxml = "Malformed xml.";
        public static final String wrongstructure = "Wrong Plugin Structure.";
        public static final String requestclassmissing = "Request class is Missing.";
        public static final String problemloading = "Problem Loading Plugin Classes.";
        
        private PluginException(String malformedxml) {
            super(malformedxml);
        }
    }
    
}
