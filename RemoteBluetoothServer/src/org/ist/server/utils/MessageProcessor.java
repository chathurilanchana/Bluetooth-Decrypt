package org.ist.server.utils;

import org.ist.server.crypto.Crypto;
import org.ist.server.crypto.KEKGenerator;

public class MessageProcessor {
	private final String LOGIN_PREFIX = "LG";
	private final String SEP_MSG = ";";
	private final String SEP_PIPE = ":";

	/**
	 * @param args
	 */

	public void processReceivedString(String receivedMsg) {
		try {
			KEKGenerator pwdHash = new KEKGenerator();
            Crypto crypto=new Crypto();
			
			String[] messageType = receivedMsg.split(SEP_PIPE);
			if (messageType.length ==2 && messageType[0].equals(LOGIN_PREFIX) ) {
				// This is a login message
				// plaintext
				String username = messageType[1];
				System.out.println("username is " + username);
				KEKGenerator generator=new KEKGenerator();
				String hash=generator.createHash("chathuri");
				System.out.println(hash);
				hash=generator.createHash("chathuri");
				System.out.println(hash);
				
				
			} else {
				
			
				// Totally encrypted message, receiving a nonce
				String KEKInPC="";//This should be read after the registration part
				String pwdInPC="";//this should be read after reg. Password stored in db as hash
               //String decryptedText=crypto.decrypt(messageType[1], KEKInPC);
				
				//String password=decryptedText.split(":")[1];
				//hash the username. Read from db whether 2 are equal
			    //hash the pwd. Read from db whether 2 are equal
				//If both true, user authenticated. Then send the Nonse and session key encrypted by KEK. 

			}
		} catch (Exception ex) {
			System.out.println("exception while login");
			ex.printStackTrace();
		}
	}

}
