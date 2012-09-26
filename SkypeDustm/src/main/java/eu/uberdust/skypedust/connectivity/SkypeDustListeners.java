/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.connectivity;

import com.skype.api.Conversation;
import com.skype.api.Message;
import eu.uberdust.skypedust.LogFiles;
import eu.uberdust.skypedust.requestformater.RequestHanlder;
import eu.uberdust.skypedust.util.JavaTutorialListeners;
import eu.uberdust.skypedust.util.MySession;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class SkypeDustListeners extends JavaTutorialListeners {

    private RequestHanlder requestHanlder;    
    
    public SkypeDustListeners(MySession mySession){
        super(mySession);        
    }
    
    public void setcommandListener(RequestHanlder commandListener){
        this.requestHanlder = commandListener;
    }
    
    public void OnMessage(Message message,
			boolean changesInboxTimestamp, Message supersedesHistoryMessage, Conversation conversation){
		Message.TYPE msgType = Message.TYPE.get(message.GetIntProperty(Message.PROPERTY.type));

        if (msgType == Message.TYPE.POSTED_TEXT) {
            String msgAuthor = message.GetStrProperty(Message.PROPERTY.author);
            String msgBody = message.GetStrProperty(Message.PROPERTY.body_xml);
            if (!msgAuthor.equals(mySession.myAccountName)) {
                // Get timestamp -- it's in seconds, and the Date constructor needs milliseconds!
            	Integer msgTimeStamp = new Integer(message.GetIntProperty(Message.PROPERTY.timestamp));
                Date dateTimeStamp = new Date((msgTimeStamp.longValue() * 1000L));
            	DateFormat targetDateFmt = DateFormat.getDateTimeInstance();
                String reply=null;
            	System.out.print("Message posted from: "+msgAuthor+"\n"+
                       "Text "+msgBody);
                LogFiles.writeMessageLog(msgAuthor,msgBody);                
                Calendar targetDate = Calendar.getInstance();
                if(requestHanlder!=null){
                    reply = requestHanlder.inputParse(msgAuthor, msgBody);
                }
                else{
                    reply = "SkypeDust service not initialized properly";
                }
            	conversation.PostText((targetDateFmt.format(targetDate.getTime()) + ": "+reply), false);
            }
        }
		else {
			MySession.myConsole.printf("%s: Ignoring SkypeListener.OnMessage of type %s%n",
					mySession.myTutorialTag, msgType.toString());
		}	
    }
}
