/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.util.Random;
import java.util.UUID;
import org.ist.server.crypto.AssymetricEncryptionHandler;
import org.ist.server.crypto.Crypto;
import org.ist.server.crypto.KEKGenerator;

/**
 *
 * @author Chathuri
 */
public class MessageProcessor {

    private final String LOGIN_PREFIX = "LG";
    private final String SESSION_PREFIX = "SK";
    private final String NONSE_STRING = "NS";
    private final String SEP_MSG = ";";
    private final String SEP_PIPE = ":";
    private int nounce=0;

    /**
     * @param userName
     * @param privateKeyPath
     * @param receivedMsg
     * @param args
     * @return
     */
    /* public String processReceivedString(String receivedMsg) {
     try {

     String[] messageType = receivedMsg.split(SEP_PIPE);
     if (messageType.length == 2 && messageType[0].equals(LOGIN_PREFIX)) {
     // This is a login message
     // plaintext
     String username = messageType[1];
     String message = LOGIN_PREFIX + SEP_PIPE + username;
     return message;

     } else {
     // Totally encrypted message, receiving a nonce
     String KEKInPC = "";//This should be read after the registration part
     String pwdInPC = "";//this should be read after reg. Password stored in db as hash
     // KEKGenerator pwdHash = new KEKGenerator();
     // Crypto crypto=new Crypto();
     //String decryptedText=crypto.decrypt(messageType[1], KEKInPC);

     //String password=decryptedText.split(":")[1];
     //hash the username. Read from db whether 2 are equal
     //hash the pwd. Read from db whether 2 are equal
     //If both true, user authenticated. Then send the Nonse and session key encrypted by KEK.
     //if decrypt, display the message to user
     return "";
     }
     } catch (Exception ex) {
     System.out.println("exception while login");
     return "error while login";
     }

     }*/
     /**
     * @param userName
     * @param privateKeyPath
     * @return
     */
    public String getKEK(String userName,String privateKeyPath) {
        DBConnector connector = new DBConnector();
        String KEK= connector.retrieveKEKForUser(userName);
        AssymetricEncryptionHandler assymHandler=new AssymetricEncryptionHandler();
        return assymHandler.decryptByPrivateKey(KEK,privateKeyPath);
    }


    public String generatePlainMsgWithSession(String sessionKey) {
        this.nounce = new Random().nextInt(400000);
        String plainText = SESSION_PREFIX.concat(SEP_PIPE).concat(sessionKey).concat(SEP_MSG).concat(NONSE_STRING).concat(SEP_PIPE).concat(Integer.toString(getNounce()));
        return plainText;
    }
    
    public String generatePlainNounceMessage(){
        this.nounce=(nounce==0)?new Random().nextInt(100000):nounce+2;
        String plainMessage=NONSE_STRING.concat(SEP_PIPE).concat(Integer.toString(nounce));
        return plainMessage;
    }

    public String generateSessionKey() {
        KEKGenerator kekGen = new KEKGenerator();
        String uuId = UUID.randomUUID().toString();
        return kekGen.GetSHAHash(uuId);

    }
    
 

    public String generateEncryptedMsg(String plainReply,String key) {
        Crypto crypto = new Crypto();

        if (key.equals("NOUSER")) {
            return "NOUSER";
        }
        String encrptedText = crypto.getEncryptedMessage(plainReply, key);
        return encrptedText;
    }

    /**
     * @return the nounce
     */
    public int getNounce() {
        return nounce;
    }

    /**
     * @param nounce the nounce to set
     */
    public void setNounce(int nounce) {
        this.nounce = nounce;
    }

    public boolean isNonceCorrect(String decryptedClientMessage) {
      String nounceStr=decryptedClientMessage.split(SEP_PIPE)[1];
      int nounceFromClient=Integer.parseInt(nounceStr);
      if(nounceFromClient!=getNounce()+1){
          return false;
      }
      return true;
     
    }

    public String getFolderEncryptionKey(String userName, String privateKeyPath) {
        DBConnector connector = new DBConnector();
        String encryptedFolderEncryptionKey= connector.retrieveFileEncryptionKeyForUser(userName);
        AssymetricEncryptionHandler assymHandler=new AssymetricEncryptionHandler();
        return assymHandler.decryptByPrivateKey(encryptedFolderEncryptionKey,privateKeyPath);
    }
}
