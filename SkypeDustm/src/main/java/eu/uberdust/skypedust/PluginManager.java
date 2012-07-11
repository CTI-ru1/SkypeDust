/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.requestformater.DefaultRequest;
import eu.uberdust.skypedust.requestformater.RequestInterface;

/**
 *
 * @author gkatzioura
 */
public class PluginManager {

    public static void AutoMode() {

    }
    
    public static void addrequestFormatter(String pluginpath){
    
    }
    
    public static RequestInterface requestFormatter() {
    
        return new DefaultRequest();
    }
    
    
}
