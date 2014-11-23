/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.util.Random;
import java.util.UUID;
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

    /**
     * @param receivedMsg
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

    }

    public String getKEK(String userName) {
        DBConnector connector = new DBConnector();
        return connector.retrieveKEKForUser(userName);
    }

    public String generatePlainReplyMessage(String processedMsg, String messagePrefix) {
        switch (messagePrefix) {
            case LOGIN_PREFIX:
                String username = processedMsg.split(":")[1];
                System.out.println(username);

                String plainReply = generatePlainMsgWithSession();
              //String encryptedReply=generateEncryptedSessionMsg(plainReply,kek);
                //return encryptedReply;
                return plainReply;
            case NONSE_STRING:
                break;
        }
        return null;
    }

    private String generatePlainMsgWithSession() {
        String sessionKey = getSessionKey();
        int nounce = new Random().nextInt(100000);
        String plainText = SESSION_PREFIX.concat(SEP_PIPE).concat(sessionKey).concat(SEP_MSG).concat(NONSE_STRING).concat(SEP_PIPE).concat(Integer.toString(nounce));
        System.out.println("Plain text is " + plainText);
        return plainText;
    }

    private String getSessionKey() {
        KEKGenerator kekGen = new KEKGenerator();
        String uuId = UUID.randomUUID().toString();
        String sessionKey = kekGen.GetSHAHash(uuId);
        return sessionKey;
    }

    public String generateEncryptedSessionMsg(String plainReply, String messageType, String userName) {
        Crypto crypto = new Crypto();
        String kek = getKEK(userName);
        System.out.println("kek is " + kek);

        if (kek.equals("NOUSER")) {
            return "NOUSER";
        }
        String encrptedText = crypto.getEncryptedMessage(plainReply, kek);
        System.out.println("encrypted msg " + encrptedText);
        return encrptedText;
    }
}
