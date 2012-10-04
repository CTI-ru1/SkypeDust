/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

/**
 *
 * @author carnage
 */

import eu.uberdust.skypedust.pojos.CapabilityNickname;
import eu.uberdust.skypedust.pojos.NodeNickname;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataProvider {
    
    private String framework = "embedded";
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby";
    private String dbName = "skypedustDB";
    private Connection connection;
    private Statement statement;
    
    private static final String[][] createTablesQueries = new String[][] {
        {"NODE" ,"CREATE TABLE NODE(REALNAME VARCHAR(100),NICKNAME VARCHAR(100))"},
        {"CAPABILITY" ,"CREATE TABLE CAPABILITY(REALNAME VARCHAR(100),NICKNAME VARCHAR(100))"},
        {"ACCOUNT", "CREATE TABLE ACCOUNT(USERNAME VARCHAR(100))"},
        {"ALLOWEDCONTACT","CREATE TABLE ALLOWEDCONTACT(CONTACT VARCHAR(100),USERNAME VARCHAR(100))"},
        {"CONTACTSUBSCRIBED","CREATE TABLE CONTACTSUBSCRIBED(CONTACT VARCHAR(100),NODE VARCHAR(100),CAPABILITY VARCHAR(100))"},
        {"PLUGIN", "CREATE TABLE PLUGIN(NAME VARCHAR(100),TYPE VARCHAR(100),PATH VARCHAR(3200),ENABLED BOOLEAN)"}};
    
    private static final String[][] dropTablesQueries = new String[][] {
        {"NODE","DROP TABLE NODE"},
        {"CAPABILITY","DROP TABLE CAPABILITY"},
        {"ACCOUNT","DROP TABLE ACCOUNT"},
        {"ALLOWEDCONTACT","DROP TABLE ALLOWEDCONTACT"},
        {"CONTACTSUBSCRIBED","DROP TABLE CONTACTSUBSCRIBED"},
        {"PLUGIN","DROP TABLE PLUGIN"}};
    
    public DataProvider() {
        
        try {
            loadDriver();
            connection = null;
            try {
                connection = DriverManager.getConnection(protocol+":"+dbName+";create=true");
                //dropTables();
                createTables();
            } catch (SQLException ex) {
                Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createTables() {
        
        try {
            
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for(String[] query: createTablesQueries) {
                ResultSet resultSet = databaseMetaData.getTables(null,null,query[0],null);
                if(!resultSet.next()) {
                    executeQuery(query[1]);
                }                
            }

        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void dropTables() {
        
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for(String[] query: dropTablesQueries) {
                ResultSet resultSet = databaseMetaData.getTables(null,null,query[0],null);
                if(!resultSet.next()) {
                    executeQuery(query[1]);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadDriver() throws ClassNotFoundException {
        Class.forName(driver);
    }
    
    private void executeQuery(String query) {
        
        try {
            statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int insertAccount(String username) {
            
        try {
            
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT*FROM ACCOUNT "
                    + "WHERE USERNAME='"+username+"'");
            
            if(getnumRows(resultSet)==0){
            
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO ACCOUNT(USERNAME) VALUES(?)");
                preparedStatement.setString(1, username);
                return preparedStatement.executeUpdate();            
            }
            else {
                System.out.println("Exists");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public String[] getAccounts() {
        
        try {
            statement = connection.createStatement();
            List<String> accounts;
            try (ResultSet resultSet = statement.executeQuery("SELECT*FROM ACCOUNT")) {
                accounts = new ArrayList<String>();
                while(resultSet.next()) {
                    String account = resultSet.getString("USERNAME");
                    System.out.println("To account "+account);
                    accounts.add(account);
                }
            }
            statement.close();
            
            return accounts.toArray(new String[accounts.size()]);
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public int insertAllowedContact(String contact,String username) {
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM ALLOWEDCONTACT "
                    + "WHERE CONTACT='"+contact+"' "
                    + "AND USERNAME='"+username+"'");
            
            if(getnumRows(resultSet)==0) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO ALLOWEDCONTACT(CONTACT,USERNAME) VALUES(?,?)");
                preparedStatement.setString(1, contact);
                preparedStatement.setString(2, username);
                return preparedStatement.executeUpdate();                        
            }
            else {
                System.out.println("Allowed Contact Exists");
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int insertupdateNode(String realname,String nickname) {
        
        int toret = 0;
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM NODE "
                    + "WHERE REALNAME='"+realname+"'");
            
            if(getnumRows(resultSet)==0) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO NODE(REALNAME,NICKNAME) VALUES(?,?)");
                preparedStatement.setString(1,realname);
                preparedStatement.setString(2,nickname);
                toret = preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            else {
                
                toret = statement.executeUpdate("UPDATE NODE "
                        + "SET NICKNAME='"+nickname+"' "
                        + "WHERE REALNAME='"+realname+"'");
            }
            
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }
    
    public List<NodeNickname> getnodesShortname() {
    
        try {
            
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT*FROM NODE");
            
            List<NodeNickname> nodeShortnames = new ArrayList<NodeNickname>();
            
            while(resultSet.next()) {
                
                String realname = resultSet.getString("REALNAME");
                String shortname = resultSet.getString("NICKNAME");
                nodeShortnames.add(new NodeNickname(realname, shortname));
            }

            resultSet.close();
            statement.close();
            
            return nodeShortnames;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String getnodeShortName(String realname) {
        
        String toret = null;
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM NODE "
                    + "WHERE REALNAME='"+realname+"'");
            
            while(resultSet.next()) {
                toret = resultSet.getString("NICKNAME");
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }
    
    public String getnodeRealName(String shortname) {
    
        String toret=null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM NODE "
                    + "WHERE NICKNAME='"+shortname+"'");
            
            while(resultSet.next()) {
                toret = resultSet.getString("REALNAME");
            }
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }
    
    public boolean deleteNode(String realname) {
        
        boolean toret = false;
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM NODE "
                    + " WHERE REALNAME='"+realname+"'");
            
            while(resultSet.next()) {
                resultSet.deleteRow();
                toret = true;
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public int userSubscribe(String contact,String node,String capability) {
        
        int toret = 0;
        try {
            
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CONTACTSUBSCRIBED "
                    + "WHERE CONTACT='"+contact+"' "
                    + "AND NODE='"+node+"' "
                    + "AND CAPABILITY='"+capability+"'");
            if(getnumRows(resultSet)==0) {
            
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO CONTACTSUBSCRIBED(CONTACT,NODE,CAPABILITY) VALUES(?,?,?)");
                preparedStatement.setString(1, contact);
                preparedStatement.setString(2, node);
                preparedStatement.setString(3, capability);
                toret = preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return toret;
    }
    
    public void userUnsubscribe(String contact,String node,String capability) {
        try {
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CONTACTSUBSCRIBED "
                    + "WHERE CONTACT='"+contact+"' "
                    + "AND NODE='"+node+"' "
                    + "AND CAPABILITY='"+capability+"'");
            
            while(resultSet.next()) {
                resultSet.deleteRow();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getSubsribed() {
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CONTACTSUBSCRIBED");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("CONTACT"));
                System.out.println(resultSet.getString("NODE"));
                System.out.println(resultSet.getString("CAPABILITy"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String[] getSubscribedContacts(String node,String capability) {

        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CONTACTSUBSCRIBED "
                    + "WHERE NODE='"+node+"' "
                    + "AND CAPABILITY='"+capability+"'");
            
            System.out.println("node "+node+" capability "+capability);

            List<String> contacts = new ArrayList<>();
            
            while(resultSet.next()) {
                
                contacts.add(resultSet.getString("CONTACT"));
            }
            
            return contacts.toArray(new String[contacts.size()]);
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public int insertupdateCapability(String realname,String nickname) {

        int toret = 0;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CAPABILITY "
                    + "WHERE REALNAME='"+realname+"'");
            
            if(getnumRows(resultSet)==0) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO CAPABILITY(REALNAME,NICKNAME) VALUES(?,?)");
                preparedStatement.setString(1,realname);
                preparedStatement.setString(2,nickname);
                toret = preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            else {
                
                toret = statement.executeUpdate("UPDATE CAPABILITY "
                        + "SET NICKNAME='"+nickname+"' "
                        + "WHERE REALNAME='"+realname+"'");                    
            }
            
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        return toret;                
    }

    public List<CapabilityNickname> getcapabilitiesNickname(){
        
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CAPABILITY");
            List<CapabilityNickname> capabilityNicknames = new ArrayList<CapabilityNickname>();
            
            while(resultSet.next()) {
                
                String realname = resultSet.getString("REALNAME");
                String nickname = resultSet.getString("NICKNAME");
                capabilityNicknames.add(new CapabilityNickname(realname, nickname));
            }
            
            resultSet.close();
            statement.close();
            
            return capabilityNicknames;
                    
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String getcapabilityShortName(String realname) {
        
        String toret = null;
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CAPABILITY "
                    + "WHERE REALNAME='"+realname+"'");
            
            while(resultSet.next()) {
                toret = resultSet.getString("NICKNAME");
            }
                
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }

    public String getcapabilityRealName(String shortname) {
    
        String toret=null;
        try {
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CAPABILITY "
                    + "WHERE NICKNAME='"+shortname+"'");
            
            while(resultSet.next()) {
                toret = resultSet.getString("REALNAME");
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }    
    
    public boolean deleteCapability(String realname) {
        
        boolean toret = false;
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM CAPABILITY "
                    + " WHERE REALNAME='"+realname+"'");
            
            while(resultSet.next()) {
                resultSet.deleteRow();
                toret = true;
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return toret;
    }    
    
    public boolean removeAllowedContact(String username,String contact) {
        
        try {
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            
            String query = "SELECT*FROM ALLOWEDCONTACT"
                    + " WHERE CONTACT='"+contact
                    +"' AND USERNAME='"+username+"'";
            System.out.println(query);
            
            ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()) {
                resultSet.deleteRow();
                return true;
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public String[] getAllowedContacts(String username) {
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM ALLOWEDCONTACT WHERE USERNAME='"+username+"'");
            
            List<String> accounts = new ArrayList<String>();
            
            while(resultSet.next()) {
                String account = resultSet.getString("CONTACT");
                System.out.println("To allowed account "+account);
                accounts.add(account);
            }
            
            resultSet.close();
            statement.close();
            
            return  accounts.toArray(new String[accounts.size()]);
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
        
    public String[][] getPlugins() {
        
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM PLUGIN");
            
            List<String[]> plugins = new ArrayList<>();
            
            while(resultSet.next()) {
                
                String[] plugin = new String[4];
                plugin[0] = resultSet.getString("NAME");
                plugin[1] = resultSet.getString("TYPE");
                plugin[2] = resultSet.getString("PATH");
                if(resultSet.getBoolean("ENABLED")) {
                    plugin[3]="Enabled";
                }
                else {
                    plugin[3]="Disabled";
                }
                
                plugins.add(plugin);
            }
            
            resultSet.close();
            statement.close();
            
            return plugins.toArray(new String[plugins.size()][4]);
                    
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String[] getPlugin(String name) {
        
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM PLUGIN "
                    + "WHERE NAME='"+name+"'");
            String[] plugin = new String[4];
            while(resultSet.next()) {
                plugin[0] = resultSet.getString("NAME");
                plugin[1] = resultSet.getString("TYPE");
                plugin[2] = resultSet.getString("PATH");
                if(resultSet.getBoolean("ENABLED")) {
                    plugin[3] = "Enabled";
                } 
                else {
                    plugin[3] = "Disabled";
                }
            }
            
            resultSet.close();
            statement.close();
            
            return plugin;
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String getenabledPluginpath(String type) {
        
        String path = null;
        
        try {
            
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT PATH FROM PLUGIN "
                    + "WHERE TYPE='"+type+"' "
                    + "AND ENABLED=TRUE");
            
            while(resultSet.next()) {
                path = resultSet.getString("PATH");
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return path;
    }
    
    public int addPlugin(String name,String type,String path) {
        
        int toret = 0;
        
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM PLUGIN "
                    + "WHERE NAME='"+name+"' "
                    + "OR PATH='"+path+"'");
            
            if(getnumRows(resultSet)==0) {
                
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO PLUGIN(NAME,TYPE,PATH,ENABLED) VALUES(?,?,?,?)");
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,type);
                preparedStatement.setString(3,path);
                preparedStatement.setBoolean(4, false);
                toret = preparedStatement.executeUpdate(); 
            }
            
            resultSet.close();
            statement.close();
                        
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }
    
    public int enabledisPlugin(String name,Boolean enabledis) {
        
        int toret = 0;
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM PLUGIN "
                    + "WHERE NAME='"+name+"'");
            if(getnumRows(resultSet)==1) {
                toret = statement.executeUpdate("UPDATE PLUGIN "
                        + "SET ENABLED="+enabledis.toString()+" "
                        + "WHERE NAME='"+name+"'");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toret;
    }
    
    public void removePlugin(String name) {
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery("SELECT*FROM PLUGIN "
                    + "WHERE NAME='"+name+"'");
            
            while(resultSet.next()) {
                resultSet.deleteRow();
            }
            
            resultSet.close();
            statement.close();
                    
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private int getnumRows(ResultSet resultSet) {
        
        int size = 0;
        try {
            while(resultSet.next()){
                size++;
            }
            /*
            try {
                resultSet.last();
                size = resultSet.getRow();
                resultSet.beforeFirst();
            } catch (SQLException ex) {
                Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return size;
    } 
    
    public void close(){
        
        try {
            if(statement!=null) {
                statement.close();
            }
            if(connection!=null) {
                connection.close();
            }
        }
        catch(SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
