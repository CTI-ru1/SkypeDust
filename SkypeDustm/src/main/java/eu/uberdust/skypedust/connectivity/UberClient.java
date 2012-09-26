/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UberClient {

    private URL uberurl;
    private HttpClient httpClient;
    private JSONParser jsonParser;
            
    private final String nodels = "/node/json";
    private final String capabilityls = "/capability/json";
    private final String wiselight = "urn:wisebed:node:capability:light/json";
    private final String nodecapabilities = "/capabilities/json";
    private final String wisetemp = "urn:wisebed:node:capability:temperature/json";
    private final String wiseprir = "urn:wisebed:node:capability:pir/json";
    private final String wisebaro = "urn:wisebed:node:capability:barometricpressure/json";
    private final String wisehumi = "urn:wisebed:node:capability:humidity/json";
    private final String wiseir = "urn:wisebed:node:capability:ir/json";
    private final String wisebattery = "urn:wisebed:node:capability:batterycharge/json";
    private final String wiseco = "urn:wisebed:node:capability:co/json";
    private final String wiseco2 = "urn:wisebed:node:capability:co2/json";
    private final String wisech4 = "urn:wisebed:node:capability:ch4/json";
    private final String wiselight1 = "urn:wisebed:node:capability:light1/json";
    
    public UberClient(String url){
       
       jsonParser = new JSONParser();
       try {
            uberurl = new URL(url);
            httpClient = new DefaultHttpClient();
        } catch (MalformedURLException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String listNodes(){
        try {
            HttpGet httpGet = new HttpGet(uberurl.toString()+nodels);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if(httpEntity != null){
               
                String nodes ="";
                
                nodes = EntityUtils.toString(httpEntity);
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(nodes);
                nodes = "";
                JSONObject jSONObject = (JSONObject)obj;
                JSONArray array =(JSONArray)jSONObject.get("nodes");
                for(int i=0;i<array.size();i++){
                    
                    System.out.println(array.get(i).toString());
                    nodes = nodes+array.get(i).toString()+"\n";
                }
                
                return nodes;
            }
            
        }catch (ParseException | IOException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "404";
    }
    
    public String listCapabilities(){
        
        String reply = basicGet(uberurl.toString()+capabilityls);
        
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(reply);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray =(JSONArray)jsonObject.get("capabilities");

            reply = "";
            
            for(int i=0;i<jsonArray.size();i++){
                
                reply = reply+(String) jsonArray.get(i)+"\n";
            }
            
        } catch (ParseException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reply;
    }

    public String getnodeCapabilities(String node) {

        String customurl = uberurl.toString()+"/node/"+node+nodecapabilities;
        String reply = basicGet(customurl);
        
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(reply);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray)jsonObject.get("capabilities");
            
            reply = "";
            
            for(int i=0;i<jsonArray.size();i++) {
            
                reply = reply+(String)jsonArray.get(i)+"\n";
            }
            
        } catch (ParseException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reply;
    }

    public String getnodeCapability(String node,String capability,Integer limit) {
        
        String customurl = uberurl.toString()+"/node/"+node+"/capability/"
                +getcapabilityName(capability)+"/json/limit/"+limit.toString();
        
        System.out.println("The url: "+customurl);
        String reply = basicGet(customurl);
        try {
            Object obj = jsonParser.parse(reply);
            JSONObject jobj = (JSONObject)obj;
            JSONArray array = (JSONArray)jobj.get("readings");
            String status = "";
            
            for(int i=0;i<array.size();i++) {
                JSONObject jObject = (JSONObject)array.get(i);
                status = "timpestamp "+ jObject.get("timestamp")+"\n";
                status = status+"reading "+jObject.get("reading")+"\n";
            }
            
            return status;
        } catch (ParseException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return reply;
    }
    
    public String setTonodecapability(String node,String capability,String reading) {
        
        String customurl = uberurl.toString()+"/node/"+node
                +"/capability/"+getcapabilityName(capability) +"/insert/timestamp/"
                +unixTimestamp()+"/reading/"+reading+"/";

        System.out.println("The url: "+customurl);
        return basicGet(customurl);

    }
    
    public String getnodeLight(String node,Integer limit){

        String customurl = uberurl.toString()+"/node/"+node+"/capability/"+wiselight+"/limit/"+limit.toString();
        String reget = basicGet(customurl);
            
        try {
            Object obj = jsonParser.parse(reget);
            JSONObject jobj = (JSONObject)obj;
            JSONArray array = (JSONArray) jobj.get("readings");
            
            String lightstatus="";
            for(int i=0 ;i<array.size();i++){
                JSONObject joObject = (JSONObject) array.get(i);
                lightstatus = "timestamp "+joObject.get("timestamp")+"\n";
                lightstatus = lightstatus+"reading "+joObject.get("reading")+"\n";
            }
                    
            return lightstatus;            
        } catch (ParseException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reget;
    }
    
    public String getnodeTemperature(String node,Integer limit){
    
        String customurl = uberurl.toString()+"/node/"+node+"/capability/"+wisetemp+"/limit/"+limit.toString();
        String reget = basicGet(customurl);
        try {
            Object obj = jsonParser.parse(reget);
            JSONObject jobj = (JSONObject)obj;
            JSONArray array = (JSONArray) jobj.get("readings");
            String tempstatus="";
            for(int i=0 ;i<array.size();i++){
                JSONObject joObject = (JSONObject) array.get(i);
                tempstatus = "timestamp "+joObject.get("timestamp")+"\n";
                tempstatus = tempstatus+"reading "+joObject.get("reading")+"\n";
            }
                    
            return tempstatus;
        } catch (ParseException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reget;
    }
    
    /*
     * Setting node temperature 
     */
    
    public String setNodeTemperature(String node,String reading) {
        
        String customurl = uberurl.toString()+"/node/"+node
                +"/capability/urn:wisebed:node:capability:temperature/insert/timestamp/"
                +unixTimestamp()+"/reading/"+reading+"/";
        System.out.println("The url: "+customurl);
        return basicGet(customurl);
    }
    
    /*
     * Setting the ligt reading
     */
    
    public String setnodeLight(String node,String reading) {
        
        String customurl = uberurl.toString()+"/node/"+node
                +"/capability/urn:wisebed:node:capability:light/insert/timestamp/"
                +unixTimestamp()+"/reading/"+reading+"/";
        return basicGet(customurl);
    }
    
    /*
     *  In case we want to turn on/off the light
     */
    
    public String setnodeLight(String node,boolean onoff) {
    
        String reading;
        if(onoff) {
            reading="1";
        }
        else {
            reading = "0";
        }
        
        String customurl = uberurl.toString()+"/node/"+node
                + "/capability/urn:wisebed:node:capability:light/insert/timestamp/"
                + unixTimestamp() +"/reading/"+reading+"/";
        
        return customurl;
    }
    
    private String getcapabilityName(String capability) {
    
        switch(capability) {
            
            case "lz2":
                return "urn:wisebed:node:capability:lz2";
            case "light":
                return "urn:wisebed:node:capability:light";
            case "sda:temperature":
                return "urn:wisebed:ctitestbed:node:capability:sda:temperature";
            case "status":
                return "urn:wisebed:ctitestbed:node:capability:status";
            case "light5":
                return "urn:wisebed:node:capability:light5";
            case "ba":
                return "urn:wisebed:node:capability:ba";
            case "tasks":
                return "urn:wisebed:ctitestbed:node:capability:tasks";
            case "ch4":
                return "urn:wisebed:node:capability:ch4";
            case "y":
                return "y";
            case "usage":
                return "urn:wisebed:ctitestbed:node:capability:md2:usage";
            case "humidity":
                return "urn:wisebed:node:capability:humidity";
            case "lz3":
                return "urn:wisebed:node:capability:lz3";
            case "temperatu":
                return "urn:wisebed:node:capability:temperatu";
            case "sdb:temperature":
                return "urn:wisebed:ctitestbed:node:capability:sdb:temperature";
            case "lz4":
                return "urn:wisebed:node:capability:lz4";
            case "lz5":
                return "urn:wisebed:node:capability:lz5";
            case "batterycharge":
                return "urn:wisebed:node:capability:batterycharge";
            case "temp":
                return "urn:wisebed:node:capability:temp";
            case "pir":
                return "urn:wisebed:node:capability:pir";
            case "lqi":
                return "urn:wisebed:node:capability:lqi";
            case "lz1":
                return "urn:wisebed:node:capability:lz1";
            case "x":
                return "x";
            case "lockScreen":
                return "urn:wisebed:ctitestbed:node:capability:lockScreen";
            case "sda1:usage":
                return "urn:wisebed:ctitestbed:node:capability:sda1:usage";
            case "led":
                return "urn:wisebed:node:capability:led";
            case "co2":
                return "urn:wisebed:node:capability:co2";
            case "ir":
                return "urn:wisebed:node:capability:ir";
            case "memfree":
                return "urn:wisebed:ctitestbed:node:capability:memfree";
            case "md1:usage":
                return "urn:wisebed:ctitestbed:node:capability:md1:usage";
            case "md0:usage":
                return "urn:wisebed:ctitestbed:node:capability:md0:usage";
            case "lz0":
                return "urn:wisebed:node:capability:lz0";
            case "swapfree":
                return "urn:wisebed:ctitestbed:node:capability:swapfree";
            case "sdb4:usage":
                return "urn:wisebed:ctitestbed:node:capability:sdb4:usage";
            case "li":
                return "urn:wisebed:node:capability:li";
            case "description":
                return "description";
            case "room":
                return "room";
            case "co":
                return "urn:wisebed:node:capability:co";
            case "cor":
                return "urn:wisebed:node:capability:.well-known:cor";
            case "barometricpressure":
                return "urn:wisebed:node:capability:barometricpressure";
            case "light2":
                return "urn:wisebed:node:capability:light2";
            case "light4":
                return "urn:wisebed:node:capability:light4";
            case "uptime":
                return "urn:wisebed:ctitestbed:node:capability:uptime";
            case "z":
                return "z";
            case "users":
                return "urn:wisebed:ctitestbed:node:capability:users";
            case "batterycurrent":
                return "urn:wisebed:node:capability:batterycurrent";
            case "report":
                return "urn:wisebed:node:capability:report";
            case "nodetype":
                return "nodetype";
            case "workstation":
                return "workstation";
            case "core":
                return "urn:wisebed:node:capability:.well-known:core";
            case "sda2:usage":
                return "urn:wisebed:ctitestbed:node:capability:sda2:usage";
            case "sda:usage":
                return "urn:wisebed:ctitestbed:node:capability:sda:usage";
        }
        
        return null;
    }
    
    private String basicGet(String geturl){
        try {
            HttpGet httpGet = new HttpGet(geturl);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if(httpEntity!=null)
                return EntityUtils.toString(httpEntity);
        } catch (IOException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "404";
    }
    
    private String unixTimestamp() {
        
        return new Long(System.currentTimeMillis() / 1000L).toString();    
    }
    
}