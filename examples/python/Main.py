'''
Created on Nov 20, 2011

@author: Gkatziouras Emmanouil(gkatzioura)
'''

import sys
import keypair,Skype
from time import sleep

loggedIn = False

def AccountOnChange (self, property_name):
    global loggedIn
    if property_name == 'status':
        print ('Login sequence: ' + self.status);
        if self.status == 'LOGGED_IN':
            loggedIn = True
        if self.status == 'LOGGED_OUT':
            loggedIn = False

def OnMessage(self, message, changesInboxTimestamp, supersedesHistoryMessage, conversation):
    if message.author != accountName:
        print(message.author_displayname + ': ' + message.body_xml);
        conversation.PostText('Automated reply Yo.', False);

if __name__ == '__main__':
    print("skypy")
    
    accountName = "yourusername"
    accoutPsw = "yourpassword"
    
    try:
        MySkype = Skype.GetSkype(keypair.keyFileName)
    except:
        print("no skype instanse")
        
    Skype.Account.OnPropertyChange = AccountOnChange
    
    myaccount = MySkype.GetAccount(accountName)
    myaccount.LoginWithPassword(accoutPsw,False,False)
    
    while not loggedIn:
        sleep(1)
    
    skypeContactGroup = MySkype.GetHardwiredContactGroup("ALL_KNOWN_CONTACTS")
    skypeContacts = skypeContactGroup.GetContacts();
        
    for scontact in skypeContacts:
        print(scontact.displayname)
    
    userchat = str(raw_input("choose a user to chat; "))
    
    conversation = MySkype.GetConversationByIdentity(userchat)
    
    while True:
        sendtext = str(raw_input("send text: "))
        conversation.PostText(sendtext,False)

    #Skype.Conversation
    
    MySkype.stop()