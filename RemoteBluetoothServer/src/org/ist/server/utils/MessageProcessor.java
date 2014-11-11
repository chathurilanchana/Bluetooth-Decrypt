package org.ist.server.utils;

import org.ist.server.crypto.PasswordHash;

public class MessageProcessor {
private final String LOGIN_PREFIX="LG";
private final String SEP_PIPE=":";
	/**
	 * @param args
	 */

	public void processReceivedString(String receivedMsg) {		
		try{
		String messageType=receivedMsg.split(SEP_PIPE)[0];
		
		switch(messageType){
		case("LG"):
    	PasswordHash pwdHash=new PasswordHash();
    	String generatedHash=pwdHash.createHash(receivedMsg);
    	System.out.println(generatedHash);
    	Boolean isValid=pwdHash.validatePassword(receivedMsg, generatedHash);
    	System.out.println("validity is "+isValid);
		break;
    	
		}
		}
		catch(Exception ex){
			System.out.println("exception while login");
			ex.printStackTrace();
		}
	}

}
