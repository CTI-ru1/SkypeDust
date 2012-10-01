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
    
    private String requestHandler;

    public String getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(String requestHandler) {
        this.requestHandler = requestHandler;
    }

    public String getWebsocketHanlder() {
        return websocketHanlder;
    }

    public void setWebsocketHanlder(String websocketHanlder) {
        this.websocketHanlder = websocketHanlder;
    }
    private String websocketHanlder;
}
