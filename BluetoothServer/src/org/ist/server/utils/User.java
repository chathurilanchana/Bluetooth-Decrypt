/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.util.Date;

/**
 *
 * @author Chathuri
 */
public class User {
  private String userName;
  private String password;
  private String KEK;
  private String fileEncryptionKey;
  private Date lastUpdated;
  private String encryptedPath;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the KEK
     */
    public String getKEK() {
        return KEK;
    }

    /**
     * @param KEK the KEK to set
     */
    public void setKEK(String KEK) {
        this.KEK = KEK;
    }

    /**
     * @return the lastUpdated
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the encryptedPath
     */
    public String getEncryptedPath() {
        return encryptedPath;
    }

    /**
     * @param encryptedPath the encryptedPath to set
     */
    public void setEncryptedPath(String encryptedPath) {
        this.encryptedPath = encryptedPath;
    }

    /**
     * @return the fileEncryptionKey
     */
    public String getFileEncryptionKey() {
        return fileEncryptionKey;
    }

    /**
     * @param fileEncryptionKey the fileEncryptionKey to set
     */
    public void setFileEncryptionKey(String fileEncryptionKey) {
        this.fileEncryptionKey = fileEncryptionKey;
    }
}
