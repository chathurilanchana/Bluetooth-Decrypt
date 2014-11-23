package org.ist.server;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

import org.ist.server.crypto.KEKGenerator;
import org.ist.server.utils.MessageProcessor;


public class ProcessConnectionThread implements Runnable{

	private StreamConnection mConnection;
	
	// Constant that indicate command from devices
	private static final int EXIT_CMD = -1;
	private static final int KEY_RIGHT = 1;
	private static final int KEY_LEFT = 2;
	
	public ProcessConnectionThread(StreamConnection connection)
	{
		mConnection = connection;
	}
	
	@Override
	public void run() {
		try {
			
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();
	        
			System.out.println("waiting for input");
	        
	        while (true) {
	        	int command=0;
	        	int bytes;
                byte[] buffer = new byte[1024];
	        	inputStream.read(buffer);
	        	String receivedMsg = new String(buffer);
	        	System.out.println("Received msg is "+receivedMsg);

	        	MessageProcessor msgProcessor=new MessageProcessor();
	        	
	        	msgProcessor.processReceivedString(receivedMsg);
	        		        	
                // Send the obtained bytes to the UI Activity   
	        	 if(command == EXIT_CMD)
	        	{	
	        		System.out.println("finish process");
	        		break;
	        	}   	
	        	//processCommand(command);
        	}
        } catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	/**
	 * Process the command from client
	 * @param command the command code
	 */
	private void processCommand(int command) {
		try {
			Robot robot = new Robot();
			switch (command) {
	    	case KEY_RIGHT:
	    		robot.keyPress(KeyEvent.VK_RIGHT);
	    		System.out.println("Right");
	    		// release the key after it is pressed. Otherwise the event just keeps getting trigged	    		
	    		robot.keyRelease(KeyEvent.VK_RIGHT);
	    		break;
	    	case KEY_LEFT:
	    		robot.keyPress(KeyEvent.VK_LEFT);
	    		System.out.println("Left");
	    		// release the key after it is pressed. Otherwise the event just keeps getting trigged	    		
	    		robot.keyRelease(KeyEvent.VK_LEFT);
	    		break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
