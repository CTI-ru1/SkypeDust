/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import com.skype.api.Conversation;
import com.skype.api.Sms;
import eu.uberdust.skypedust.util.MySession;

/**
 *
 * @author carnage
 */
public class SkypeMessenger {

    private MySession skypeSession;
    
    public SkypeMessenger(MySession mySession) {
    
        skypeSession = mySession;
    }
    
    public void sendMessage(String[] contacts,String message) {
        
        Conversation conversation = skypeSession.mySkype.GetConversationByParticipants(contacts, true, true);
        
        if(conversation!=null) {
            conversation.PostText(message, true);
        }
        
        conversation.close();
    }
    
    public void sendSms(String[] contacts,String message) {
        
        Sms sms = skypeSession.mySkype.CreateOutgoingSms();
        sms.SetTargets(contacts);
        sms.SetBody(message);        
    }
    
}
