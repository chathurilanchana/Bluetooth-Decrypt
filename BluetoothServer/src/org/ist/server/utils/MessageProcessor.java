/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

/**
 *
 * @author Chathuri
 */
public class MessageProcessor {

    private final String LOGIN_PREFIX = "LG";
    private final String SEP_MSG = ";";
    private final String SEP_PIPE = ":";

    /**
     * @param args
     * @return 
     */
    public String processReceivedString(String receivedMsg) {
        try {

            String[] messageType = receivedMsg.split(SEP_PIPE);
            if (messageType.length == 2 && messageType[0].equals(LOGIN_PREFIX)) {
				// This is a login message
                // plaintext
                String username = messageType[1];
                String message="Username is "+username;
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
    
    }
}

