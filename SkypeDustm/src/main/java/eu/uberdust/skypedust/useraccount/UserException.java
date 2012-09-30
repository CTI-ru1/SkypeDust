/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.useraccount;

/**
 *
 * @author carnage
 */
public class UserException extends Exception{
    
    public static final String KeypairProblem = "Outdated Keypair or No keypair";
    public static final String RuntimeProblem = "Wrong Runtime Or Missing";
    public static final String WrongCredentials = "Wrong username or password";
    public static final String Uninitialized = "SypeDust not initialized properly";
    
    public UserException(String message){
        super(message);
    }
}
