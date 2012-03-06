/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skypedust.connectivity;

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
        try {
            HttpGet httpGet = new HttpGet(uberurl.toString()+capabilityls);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if(httpEntity != null){
                
                String capabilities ="";
                
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(EntityUtils.toString(httpEntity));
                JSONArray array = (JSONArray)obj;
                for(int i=0;i<array.size();i++){
                    JSONObject joObject = (JSONObject) array.get(i);
                    capabilities = capabilities+joObject.get("capabilityName")+"\n";
                }            
                return capabilities;
            }
        } catch (ParseException | IOException ex) {
            Logger.getLogger(UberClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "404";    
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
    
}