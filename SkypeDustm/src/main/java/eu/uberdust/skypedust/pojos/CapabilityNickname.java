/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.pojos;

/**
 *
 * @author carnage
 */
public class CapabilityNickname {
    
    private String capabilityName;
    private String nickName;

    public CapabilityNickname(String capabilityname,String nickname) {
        
        capabilityName = capabilityname;
        nickName = nickname;
    }
    
    public CapabilityNickname() {
    
    }
    
    public String getCapabilityName() {
        return capabilityName;
    }

    public void setCapabilityName(String capabilityName) {
        this.capabilityName = capabilityName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
}
