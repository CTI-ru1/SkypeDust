/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.useraccount;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import eu.uberdust.skypedust.pojos.UserSettings;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class XmlConfs {

    public static final String usernametag = "username";
    public static final String nicknametag = "nickname";
    public static final String passwordtag = "password";
    private static final String user = "user";
    private static final String friend = "friend";
    private static final String usernameatr = "username";
    
    public UserSettings readSettingsConf(String settingsFile){
        //Hashtable userSettings = new Hashtable();
        UserSettings userSettings = new UserSettings();
        InputStream instream = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            instream = new FileInputStream(settingsFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(instream);
            while(eventReader.hasNext()){
                XMLEvent event = eventReader.nextEvent();
                if(event.isStartElement()){
                    StartElement element = event.asStartElement();
                    String tag = element.getName().getLocalPart();
                    String value = eventReader.nextEvent().asCharacters().getData();
                    
                    if(tag.equals(passwordtag)) 
                        userSettings.setPassword(value);
                    if(tag.equals(nicknametag)) 
                        userSettings.setNickname(value);
                    if(tag.equals(usernametag)) 
                        userSettings.setUsername(value);
                }
            }
            instream.close();
        } catch (XMLStreamException | IOException ex) {
            Logger.getLogger(XmlConfs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userSettings;
    }
    
    public void writeServerSettings(String servverconf,String url) {
    
    }
    
    public String readServerSettings(String serverconf) {
        return null;
    }
    
    public ArrayList readuAllowedContacts(String allowedConFile,String username){
        
        ArrayList allowedContacts = new ArrayList();
        allowedContacts = readAllowedContacts(allowedConFile).get(username);
        return allowedContacts;
    }
    
    public HashMap<String,ArrayList> readAllowedContacts(String allowedConFile){

        HashMap<String,ArrayList> allowedContacts = new HashMap<>();
                
        InputStream instream = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            instream = new FileInputStream(allowedConFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(instream);
            String hashuser="";
            ArrayList hashfriends=new ArrayList();
                
            while(eventReader.hasNext()){
                XMLEvent event = eventReader.nextEvent();
                        
                if(event.isStartElement()){
                    StartElement element = event.asStartElement();
                    if(element.getName().getLocalPart().equals(user)){
                        Iterator<Attribute> attributes = element.getAttributes();
                        while(attributes.hasNext()){
                            Attribute attribute = attributes.next();
                            if(attribute.getName().toString().equals(usernameatr)){
                                hashuser=attribute.getValue().toString();
                                hashfriends = new ArrayList();
                            }
                        }  
                    }
                    if(element.getName().getLocalPart().equals(friend)){
                        event = eventReader.nextEvent();
                        hashfriends.add(event.asCharacters().getData());
                    }
                }
                if(event.isEndElement()){
                    EndElement element = event.asEndElement();
                    if(element.getName().getLocalPart().equals(user))           
                        allowedContacts.put(hashuser, hashfriends);
                }
            }
            instream.close();
        } 
        catch (XMLStreamException|IOException ex) {
            ex.printStackTrace();
        }      
        return allowedContacts;
    }
    
    public boolean  writeSettingsConf(String settingsFile,String username,String nickname,String password){
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(settingsFile));
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            StartElement startElement = eventFactory.createStartElement("","","Settings");
            eventWriter.add(startElement);
            eventWriter.add(end);
            createNode(eventWriter,"username",username);
            createNode(eventWriter,"nickname",nickname);
            createNode(eventWriter,"password", password);
            eventWriter.add(eventFactory.createEndElement("","","Settings"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
            return true;
        } catch (FileNotFoundException | XMLStreamException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean writeAllowedContacts(String allowedContactsFile,String[] allowedContacts,String username){
        
        HashMap<String,ArrayList> refreshed = refreshHash(allowedContactsFile, allowedContacts, username);                
        Iterator<String> hasuser = refreshed.keySet().iterator();

        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(allowedContactsFile));
            XMLEventFactory eventFactory = XMLEventFactory.newInstance(); 
            XMLEvent end = eventFactory.createDTD("\n");
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(end);
            StartElement startElement = eventFactory.createStartElement("","","AllowedContacts");
            eventWriter.add(startElement);
            eventWriter.add(end);
            while(hasuser.hasNext()){
                String hell = hasuser.next();
                System.out.println(hell);
                StartElement userElement = eventFactory.createStartElement("","",user);
                eventWriter.add(userElement);
                XMLEvent userattrib = eventFactory.createAttribute(usernameatr,hell);
                eventWriter.add(userattrib);
                eventWriter.add(end);
                ArrayList arrayList = refreshed.get(hell);
                for(int i=0;i<arrayList.size();i++){
                    System.out.println(arrayList.get(i));
                    createNode(eventWriter,friend,allowedContacts[i]);
                }
                eventWriter.add(eventFactory.createEndElement("","",user));
                eventWriter.add(end);
            }
            eventWriter.add(eventFactory.createEndElement("","","AllowedContacts"));
            eventWriter.add(end);
            eventWriter.close();
            return true;
        } catch (FileNotFoundException|XMLStreamException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private HashMap<String,ArrayList> refreshHash(String allowedContactsFile,String[] allowedContacts,String username){
        
        HashMap<String,ArrayList> oldhash = readAllowedContacts(allowedContactsFile);
        
        ArrayList arrayList = new ArrayList();
        for(int i=0;i<allowedContacts.length;i++)
            arrayList.add(allowedContacts[i]);
        oldhash.put(username, arrayList);
        
        return oldhash;
    }
    
    private boolean createNode(XMLEventWriter eventWriter,String name,String value){
   
        try {
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent tab = eventFactory.createDTD("\t");
            StartElement startElement = eventFactory.createStartElement("","",name);
            eventWriter.add(tab);
            eventWriter.add(startElement);
            Characters characters = eventFactory.createCharacters(value);
            eventWriter.add(characters);
            EndElement endElement = eventFactory.createEndElement("","",name);
            eventWriter.add(endElement);
            eventWriter.add(end);
            return true;
        } catch (XMLStreamException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}