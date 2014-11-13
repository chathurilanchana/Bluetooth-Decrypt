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
			
			System.out.println("received message is " + receivedMsg);
			String[] messageType = receivedMsg.split(SEP_MSG);
			if (messageType.length > 1) {
				// This is an initial login message as we send username as
				// plaintext
				String username = messageType[0];
				System.out.println("username is " + username);
				System.out.println("Encrypted Message is "+messageType[1]);
				
				String KEKInPC="";//This should be read after the registration part
				String pwdInPC="";//this should be read after reg. Password stored in db as hash
               String decryptedText=crypto.decrypt(messageType[1], KEKInPC);
				
				String password=decryptedText.split(":")[1];
				//hash the username. Read from db whether 2 are equal
			    //hash the pwd. Read from db whether 2 are equal
				//If both true, user authenticated. Then send the Nonse and session key encrypted by KEK. 
			} else {
				// Totally encrypted message, a change password or nonse
				//If reply received,encrypt folder
				//continue with nonse within given time interval

			}
		} catch (Exception ex) {
			System.out.println("exception while login");
			ex.printStackTrace();
		}
	}

}
