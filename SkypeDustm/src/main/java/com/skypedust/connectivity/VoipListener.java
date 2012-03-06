/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skypedust.connectivity;

import com.skype.api.Participant;
import com.skype.api.Skype;
import com.skypedust.util.MySession;
import com.skype.api.Skype.GetAvailableOutputDevicesResult;
import com.skype.api.Skype.GetAvailableRecordingDevicesResult;

/**
 *
 * @author gkatzioura
 */
public class VoipListener {
    
    private GetAvailableOutputDevicesResult outputDevicesResult;
    private GetAvailableRecordingDevicesResult recordingDevices;
    private MySession mySession;
    
    public VoipListener(MySession mySession){
        
        
        System.out.println("Voip listener");
        outputDevicesResult = mySession.mySkype.GetAvailableOutputDevices();
        recordingDevices = mySession.mySkype.GetAvailableRecordingDevices();
        this.mySession = mySession;
    }
    
    public void setAudio(){
        
        //mySession.mySkype.SetupAudioDevices(0,0);
        for(String name : outputDevicesResult.nameList){
            System.out.println(name);
        }
        
    }
    
    /*private void printDevices(){
   
        System.out.println("Printing Devices");
        
        for(String device : outputDevicesResult.productIdList){
            System.out.println(device);
        }
    }*/
}
