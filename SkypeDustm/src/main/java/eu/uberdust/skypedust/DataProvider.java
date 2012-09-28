/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

/**
 *
 * @author carnage
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.EmbeddedDriver;

public class DataProvider {
    
    private String framework = "embedded";
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby";
    private String dbName = "skypedustDB";
    private Connection connection;
    private Statement statement;
    
    public DataProvider() {
        
        try {
            loadDriver();
            connection = null;
            try {
                connection = DriverManager.getConnection(protocol+":"+dbName+";create=true");
                //createTables();
                //dropTables();
                //createTables();
            } catch (SQLException ex) {
                Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createTables() {
        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE NODE(REALNAME VARCHAR(100),NICKNAME VARCHAR(100))");
            statement.execute("CREATE TABLE UTILITIES(REALNAME VARCHAR(100),NICKNAME VARCHAR(100))");
            statement.execute("CREATE TABLE ACCOUNT(USERNAME VARCHAR(100))");
            statement.execute("CREATE TABLE ALLOWEDCONTACT(CONTACT VARCHAR(100),USERNAME VARCHAR(100))");
            statement.execute("CREATE TABLE REGISTEREDUSER(CONTACT VARCHAR(100),NODE VARCHAR(100),CAPABILITY VARCHAR(100))");
            statement.close();
            System.out.println("Created Table");
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void dropTables() {
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE NODE");
            statement.execute("DROP TABLE UTILITIES");
            statement.execute("DROP TABLE ACCOUNT");
            statement.execute("DROP TABLE ALLOWEDCONTACT");
            statement.execute("DROP TABLE REGISTEREDUSER");
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }
    
    private void loadDriver() throws ClassNotFoundException {
        Class.forName(driver);
    }
    
    public int insertAccount(String username) {
            
        try {
            
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT*FROM ACCOUNT WHERE USERNAME='"+username+"'");
            
            if(getnumRows(resultSet)==0){
            
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ACCOUNT(USERNAME) VALUES(?)");
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
            ResultSet resultSet = statement.executeQuery("SELECT*FROM ACCOUNT");
            
            List<String> accounts = new ArrayList<String>(); 
            
            while(resultSet.next()) {
                String account = resultSet.getString("USERNAME");
                System.out.println("To account "+account);
                accounts.add(account);
            }

            resultSet.close();
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
            ResultSet resultSet = statement.executeQuery("SELECT*FROM ALLOWEDCONTACT WHERE CONTACT='"+contact+"' AND USERNAME='"+username+"'");
            
            if(getnumRows(resultSet)==0) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ALLOWEDCONTACT(CONTACT,USERNAME) VALUES(?,?)");
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
    
    public int insertRegisteredContact(String contact,String node,String capability) {
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            
            //ResultSet resultSet = statement.executeQuery("SELECT*FROM ALLOWEDCONTACT WHERE CONTACT='"+contact+"' AND USERNAME='"+username+"'");
            
            /*
            if(getnumRows(resultSet)==0) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ALLOWEDCONTACT(CONTACT,USERNAME) VALUES(?,?)");
                preparedStatement.setString(1, contact);
                preparedStatement.setString(2, username);
                return preparedStatement.executeUpdate();                        
            }
            else {
                System.out.println("Allowed Contact Exists");
            }
            
            resultSet.close();
            statement.close();*/
            
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    private int getnumRows(ResultSet resultSet) {
        
        int size = 0;
        try {
            resultSet.last();
            size = resultSet.getRow();
            resultSet.beforeFirst();
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
