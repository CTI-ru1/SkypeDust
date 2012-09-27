/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

/**
 *
 * @author carnage
 */
public abstract class UberdustClient {

    protected SkypeMessenger skypeMessenger;

    public SkypeMessenger getSkypeMessenger() {
        return skypeMessenger;
    }

    public void setSkypeMessenger(SkypeMessenger skypeMessenger) {
        this.skypeMessenger = skypeMessenger;
    }    
    
    protected String getcapabilityName(String capability) {
    
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

    protected String unixTimestamp() {
        
        return new Long(System.currentTimeMillis() / 1000L).toString();    
    }
    
    protected Long secsUntil(String date) {
    
        
        return null;
    }
    
}
