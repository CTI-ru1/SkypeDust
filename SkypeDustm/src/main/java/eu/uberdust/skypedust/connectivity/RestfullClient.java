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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestfullClient extends UberdustClient {

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
    
    public RestfullClient(String url){
       
       jsonParser = new JSONParser();
       try {
            uberurl = new URL(url);
            httpClient = new DefaultHttpClient();
        } catch (MalformedURLException ex) {
            Logger.getLogger(RestfullClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String listNodes(){
        try {
            System.out.println(uberurl.toString()+nodels);
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
            Logger.getLogger(RestfullClient.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RestfullClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reply;
    }

    public String getnodeCapabilities(String node) {

        String customurl = uberurl.toString()+"/node/"+getnodeName(node) +nodecapabilities;
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
            Logger.getLogger(RestfullClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reply;
    }

    public String getnodeCapability(String node,String capability,Integer limit) {
        
        String customurl = uberurl.toString()+"/node/"+getnodeName(node) +"/capability/"
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
            Logger.getLogger(RestfullClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return reply;
    }
    
    public String setTonodecapability(String node,String capability,String reading) {
        
        String customurl = uberurl.toString()+"/node/"+getnodeName(node)
                +"/capability/"+getcapabilityName(capability) +"/insert/timestamp/"
                +unixTimestamp()+"/reading/"+reading+"/";

        System.out.println("The url: "+customurl);
        return basicGet(customurl);

    }
        
    private String basicGet(String geturl){
        try {
            HttpGet httpGet = new HttpGet(geturl);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if(httpEntity!=null)
                return EntityUtils.toString(httpEntity);
        } catch (IOException ex) {
            Logger.getLogger(RestfullClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "404";
    }
    
}