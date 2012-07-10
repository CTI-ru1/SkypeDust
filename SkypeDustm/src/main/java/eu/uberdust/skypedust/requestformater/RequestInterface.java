/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.requestformater;

import java.util.ArrayList;

/**
 *
 * @author gkatzioura
 */
public interface RequestInterface {

    public void setCommandCons(ArrayList<String> CommandCons);
    
    public String uberRequest(String author,String body);
}
