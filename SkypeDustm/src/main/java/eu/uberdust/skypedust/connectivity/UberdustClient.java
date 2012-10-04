/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import eu.uberdust.skypedust.DataProvider;

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
    
    protected String getnodeName(String nickname) {
    
        DataProvider dataProvider = new DataProvider();
        String realname = dataProvider.getnodeRealName(nickname);
        
        dataProvider.close();
        
        if(realname==null) {
            return nickname;
        }
        else {
            return realname;
        }
    }
    
    protected String getcapabilityName(String nickname) {
    
        DataProvider dataProvider = new DataProvider();
        String realname = dataProvider.getcapabilityRealName(nickname);
        dataProvider.close();

        if(realname==null) {
            return nickname;
        }
        else {
            return realname;
        }        
    }

    protected String unixTimestamp() {
        
        return new Long(System.currentTimeMillis() / 1000L).toString();    
    }
    
    protected Long secsUntil(String date) {
    
        
        return null;
    }
    
}
