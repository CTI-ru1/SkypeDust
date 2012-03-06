/* 
 * File:   main.cpp
 * Author: Gkatziouras Emmanouil(gkatzioura)
 *
 * Created on September 30, 2011, 12:31 PM
 */

#include <string>
#include <cstring>
#include <iostream>
#include <cstdlib>
#include "skype-embedded_2.h"
#include "keypair.h"

SEString inetAddr = "127.0.0.1";
uint portNum = 8963;

using namespace std;

class UserAcount : public Account{
public :
    typedef DRef<UserAcount,Account> Ref;
    typedef DRefs<UserAcount,Account> Refs;
    bool loggedIn;
    bool loggedOut; 
    UserAcount(unsigned int oid,SERootObject* root): Account(oid,root){
    this->loggedIn = false;
    this->loggedOut = false;    
    };
    void OnChange(int prop);
    void BlockWhileLoggingIn();
    void BlockWhileLogout();
private :
    Account::STATUS loginstatus;
};

void UserAcount::BlockWhileLoggingIn(){
    while((!UserAcount::loggedIn)&&(!UserAcount::loggedOut)){
        sleep(1);
        this->GetPropStatus(loginstatus);
        if(loginstatus==UserAcount::LOGGED_IN){
            UserAcount::loggedIn=true;
            UserAcount::loggedOut=false;
        }
        cout<<"Trying to login status: "<<loginstatus<<endl;
    } 
}

void UserAcount::BlockWhileLogout(){
    while((UserAcount::loggedIn)&&(!UserAcount::loggedOut)){
        sleep(1);
        this->GetPropStatus(loginstatus);
        if(loginstatus==UserAcount::LOGGED_OUT){
            UserAcount::loggedOut=true;
            UserAcount::loggedIn=false;
        }
        cout<<"Trying to logout status: "<<loginstatus<<endl;
    }
}

class UserContact : public Contact {
public :
    typedef DRef<UserContact,Contact> Ref;
    typedef DRefs<UserContact,Contact> Refs;
    UserContact(unsigned int oid,SERootObject* root) : Contact(oid,root){};
};

class UserContactGroup : public ContactGroup{
public :
    typedef DRef<UserContactGroup,ContactGroup> Ref;
    typedef DRefs<UserContactGroup,ContactGroup> Refs;
    UserContactGroup(unsigned int oid,SERootObject* root) : ContactGroup(oid,root){};
    
    UserContact::Refs contactlist;
};

class UserConversation : public Conversation{
public :
    typedef DRef<UserConversation,Conversation> Ref;
    typedef DRefs<UserConversation,Conversation> Refs;
    UserConversation(unsigned int oid,SERootObject* root) : Conversation(oid,root){};
    void OnMessage(const MessageRef & message);
};

void UserConversation::OnMessage(const MessageRef& message){

    Message::TYPE messageType;
    message->GetPropType(messageType);
    if(messageType==Message::POSTED_TEXT){
        SEIntList propIds;
        SEIntDict propValues;
        propIds.append(Message::P_AUTHOR);
        propIds.append(Message::P_BODY_XML);
        propValues = message->GetProps(propIds);   
    
        if(propValues[0]!="yourusername"){
        
            cout<<"Conversation with:"<<propValues[0]<<endl;
            cout<<"Text: "<<propValues[1]<<endl;
            //Message::Ref reply;
            //this->PostText("Have a reply",reply, false);
        }
    }
}

class UserSkype : public Skype{
public :
    Account* skypeAccount(int oid){
        return new UserAcount(oid,this);
    }
    ContactGroup* skypeContactGroup(int oid) {
        return new UserContactGroup(oid,this);
    }
    Contact* skypeContact(int oid){
        return new UserContact(oid,this);
    }
    Conversation* newConversation(int oid){
        return new UserConversation(oid,this);
    }
    
    UserConversation:: Refs inbox;
};


void UserAcount::OnChange(int prop){
    if(prop==Account::P_STATUS){
        Account::STATUS loginstatus;
        this->GetPropStatus(loginstatus);
        if(loginstatus==Account::LOGGED_IN){
            loggedIn = true;
            cout<<"Login Complete"<<endl;
        }
       
        else if(loginstatus==Account::LOGGED_OUT){
            Account::LOGOUTREASON logoutReason;
            this->GetPropLogoutreason(logoutReason);
            loggedOut = true;
            cout<<"Logout Complete"<<endl;
        }
    }
}

UserSkype* userSkype = 0;

int main(int argc, char** argv) {

    cout<<"Welcome to The Skype Connectivity"<<endl;
    
    string u1="yourusername";
    string u2="yourpassword";
        
    SEString username = SEString(u1.c_str());
    SEString password = SEString(u2.c_str());
    
    cout<<username<<endl;
    cout<<password<<endl;
    
    cout<<"Intializing Skype Connection"<<endl;
    
    userSkype = new UserSkype();
    getKeyPair();
    userSkype->init(keyBuf,inetAddr,portNum,"streamlog.txt");
    userSkype->start();
    
    UserAcount::Ref userAccount;
    
    if(userSkype->GetAccount(username,userAccount)){
        cout<<"Logging in"<<endl;
        userAccount->LoginWithPassword(password, false, false);
        userAccount->BlockWhileLoggingIn();
        
        UserContactGroup::Ref userContactGroup;
        userSkype->GetHardwiredContactGroup(ContactGroup::SKYPE_BUDDIES, userContactGroup);
        
        userContactGroup->GetContacts(userContactGroup->contactlist);
        fetch(userContactGroup->contactlist);
        
        for(uint i =0 ;i<userContactGroup->contactlist.size();i++){
            SEString contactName;
            userContactGroup->contactlist[i]->GetPropDisplayname(contactName);
            cout<<"Contact: "<<i<<" Name: "<<contactName<<endl;
        }
        
        userSkype->GetConversationList(userSkype->inbox,Conversation::ALL_CONVERSATIONS);
        fetch(userSkype->inbox);
        
        string userfriend;
        cout<<"Choose Person"<<endl;
        cin>>userfriend;
        
        UserConversation::Ref personConv;
        List_String names;
        names.append(userfriend.c_str());
        if(!userSkype->GetConversationByParticipants(names,personConv,true,false)){
            cout<<"No conversation"<<endl;
        }
        while(1){
            string message;
            cin>>message;
            Message::Ref reply;
            personConv->PostText(message.c_str(),reply, false);
        }
      
        userAccount->Logout(false);
        userAccount->BlockWhileLogout();
        cout<<"Logging out";
      
        
    }else{
        cout<<"Account doesn't exist"<<endl;
    };
 
    cout<<"Cleaning up.\n";
    userSkype->stop();
    return 0;
}
