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

/**
 *
 * @author Chathuri
 */
public class DBConnector {

    static String dbUrl = "jdbc:mysql://127.0.0.1:3306/bluetooth";
    String dbName = "user";
    static String userName = "root";
    static String password = "";
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
                + "KEK VARCHAR(500) NOT NULL, " + "Filepath VARCHAR(500) NOT NULL" + ")";

        try {
            preparedStatement = getConnection().prepareStatement(createQuery);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("table not created");
            return "ERROR";
        }

        String selectQuery = "SELECT Username FROM " + dbName + " WHERE Username = ?";
        try {
            preparedStatement = getConnection().prepareStatement(selectQuery);
            preparedStatement.setString(1, user.getUserName());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.isBeforeFirst()) {
                return "EXIST";
            }
        } catch (Exception ex) {
            System.out.println("error while selecting user");
            return "ERROR";
        }
//ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");

        String insertQuery = "insert into " + dbName + " VALUES (?,?,?)";

        try {
            preparedStatement = getConnection().prepareStatement(insertQuery);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getKEK());
            preparedStatement.setString(3, user.getEncryptedPath());
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("table not created");
            return "ERROR";

        }

        return "SUCCESS";
    }

    String retrieveKEKForUser(String username) {
        PreparedStatement preparedStatement = null;
        String selectQuery = "SELECT KEK FROM " + dbName + " WHERE Username = ?";
        System.out.println(selectQuery);
        try {
            preparedStatement = getConnection().prepareStatement(selectQuery);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

        
                while (rs.next()) {
                    System.out.println("row exist");
                    String KEK = rs.getString("KEK");
                    System.out.println(KEK);
                    return KEK;
                }
        } catch (Exception ex) {
            return "ERROR";
        }
        return null;
    }
}
