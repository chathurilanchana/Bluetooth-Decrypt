/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBConnector {

    static String dbUrl = "jdbc:mysql://127.0.0.1:3306/bluetooth";
    String dbName = "user";
    static String userName = "root";
    static String password = "root";
    private Connection dbConnection = null;

    public DBConnector() {
        createConnection();

    }

    private boolean createConnection() {
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            dbConnection = DriverManager.getConnection(dbUrl, userName, password);
            System.out.println("Connected database successfully...");
        } catch (Exception ex) {
            System.out.println("error while establishing the connection");
            ex.printStackTrace();
        }
        return (dbConnection != null);
    }

    private static Connection getConnection() {
        Connection conn = null;
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(dbUrl, userName, password);
            System.out.println("Connected database successfully...");
        } catch (Exception ex) {
            System.out.println("error while establishing the connection");
            ex.printStackTrace();
        }
        return conn;
    }

    public String insertUser(User user) {

        PreparedStatement preparedStatement = null;
        String createQuery = "CREATE TABLE IF NOT EXISTS " + dbName + "(Username VARCHAR(500) NOT NULL, "
                + "KEK VARCHAR(500) NOT NULL, " + "FileEncryptionKey VARCHAR(500) NOT NULL, "+ "Filepath VARCHAR(500) NOT NULL" + ")";

        try {
            preparedStatement = getConnection().prepareStatement(createQuery);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("table not created");
            return Constants.ERROR_CODE;
        }

        String selectQuery = "SELECT Username FROM " + dbName + " WHERE Username = ?";
        try {
            preparedStatement = getConnection().prepareStatement(selectQuery);
            preparedStatement.setString(1, user.getUserName());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.isBeforeFirst()) {
                return Constants.USER_EXIST_CODE;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Constants.ERROR_CODE;
        }
//ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");

        String insertQuery = "insert into " + dbName + " VALUES (?,?,?,?)";

        try {
            preparedStatement = getConnection().prepareStatement(insertQuery);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getKEK());
            preparedStatement.setString(3, user.getFileEncryptionKey());
            preparedStatement.setString(4, user.getEncryptedPath());
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
             return Constants.ERROR_CODE;

        }

        return Constants.SUCCESS_CODE;
    }

    String retrieveKEKForUser(String username) {
        PreparedStatement preparedStatement = null;
        String selectQuery = "SELECT KEK FROM " + dbName + " WHERE Username = ?";
        try {
            preparedStatement = getConnection().prepareStatement(selectQuery);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

         if (!rs.isBeforeFirst()) {
                return Constants.NO_USER_EXIST;
            }
                while (rs.next()) {
                    String KEK = rs.getString("KEK");
                    return KEK;
                }
        } catch (Exception ex) {
            return Constants.ERROR_CODE;
        }
        return null;
    }
    
    
       String retrieveFileEncryptionKeyForUser(String username) {
        PreparedStatement preparedStatement = null;
        String selectQuery = "SELECT FileEncryptionKey FROM " + dbName + " WHERE Username = ?";
        try {
            preparedStatement = getConnection().prepareStatement(selectQuery);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

         if (!rs.isBeforeFirst()) {
                return Constants.NO_USER_EXIST;
            }
                while (rs.next()) {
                    String fkey = rs.getString("FileEncryptionKey");
                    System.out.println(fkey);
                    return fkey;
                }
        } catch (Exception ex) {
            return Constants.ERROR_CODE;
        }
        return null;
    }
    
       String retrieveFolderPath(String username) {
        PreparedStatement preparedStatement = null;
        String selectQuery = "SELECT Filepath FROM " + dbName + " WHERE Username = ?";
        try {
            preparedStatement = getConnection().prepareStatement(selectQuery);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

        
                while (rs.next()) {
                    String filePath = rs.getString("Filepath");
                    return filePath;
                }
        } catch (Exception ex) {
            return Constants.ERROR_CODE;
        }
        return null;
    }

  }
