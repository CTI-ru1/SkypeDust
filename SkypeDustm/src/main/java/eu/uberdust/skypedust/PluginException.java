/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

/**
 *
 * @author carnage
 */
public class PluginException extends Exception{

    public static final String nojar = "Jar is missing";
    public static final String exformatter = "Expected formatter plugin";
    public static final String installed = "Already installed";
    public static final String notproper = "Class missing";
    
    public PluginException(String message) {
        super(message);
    }
}
