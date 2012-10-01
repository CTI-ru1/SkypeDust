/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.pojos;

/**
 *
 * @author carnage
 */
public class RequestHandlerSettings extends PluginSettings{
    
    private String requesthandlerclass;
    private String websockethanlderclass;

    public String getRequesthandlerclass() {
        return requesthandlerclass;
    }

    public void setRequesthandlerclass(String requesthandlerclass) {
        this.requesthandlerclass = requesthandlerclass;
    }

    public String getWebsocketHanlderclass() {
        return websockethanlderclass;
    }

    public void setWebsocketHanlderclass(String websocketHanlderclass) {
        this.websockethanlderclass = websocketHanlderclass;
    }
    
}
