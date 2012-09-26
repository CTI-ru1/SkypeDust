/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.useraccount.UserAccount;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gkatziouras Emmanouil (gkatzioura)
 */
public class SkypedustCmd {

    private String cmdPrefix = "SkypeDust> ";
    private UserAccount userAccount;
    
    public SkypedustCmd() {
        
        UserAccount userAccount = new UserAccount();
    }
    
    public void start() {
    
        BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));
            
        while(true){
        
            try {
                System.out.print(cmdPrefix);
                String[] arguements = bfReader.readLine().split(" ");
                String answer = arguementParse(arguements);
                System.out.println(answer);
            }
            catch (IOException ex) {
                Logger.getLogger(LogFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String arguementParse(String[] arguements) {
    
        switch(arguements[0]) {
            case "turnon":
                return cmdTurnon();
            case "turnoff":
                return cmdTurnoff();
            case "help":
                return cmdHelp();
            case "quit":
                System.exit(0);
            default:
                return "Unrecognized command";
        }
    }
    
    private String cmdTurnon() {
        
        //userAccount.initSaccount(null);
        userAccount.initSaccount();
        return null;
    }
    
    private String cmdTurnoff() {
        
        return null;
    }
    
    private String cmdHelp() {
    
        return  "userpass username password :   set username password\n"+
                "turnon :                       starts skypedust"+
                "contactadd  :                  add allowed contact\n"+
                "contactrm :                    remove contact\n"+
                "contactshow :                  shows contacts and allowed contacts\n" +
                "quit :                         exit application\n";
    }
      
    public String getCmdPrefix() {
        return cmdPrefix;
    }

    public void setCmdPrefix(String cmdPrefix) {
        this.cmdPrefix = cmdPrefix;
    }
}
