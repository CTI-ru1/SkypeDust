/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust.pojos;

/**
 *
 * @author carnage
 */
public class NodeNickname {

    private String nodeName;
    private String shortName;

    public NodeNickname() {
    }
    
    public NodeNickname(String nodename,String shortname) {
        
        nodeName = nodename;
        shortName = shortname;
    }
    
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    
}
