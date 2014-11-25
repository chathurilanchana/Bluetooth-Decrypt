/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.ui;

import java.io.InputStream;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.ist.server.utils.MessageProcessor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.OutputStream;
import org.ist.server.crypto.Crypto;
import org.ist.server.crypto.KEKGenerator;
import org.ist.server.utils.ServerUtils;

/**
 *
 * @author Chathuri
 */
public class BluetoothServer extends javax.swing.JFrame {

    private String publicKeyPath;
    private String privateKeyPath;
    StreamConnection connection = null;

    // Constant that indicate command from devices
    private static final int EXIT_CMD = -1;
    private static final int KEY_RIGHT = 1;
    private static final int KEY_LEFT = 2;
    private final String LOGIN_PREFIX = "LG";
    private final String SEP_MSG = ";";
    private final String SEP_PIPE = ":";
    private String sessionKey;
    private String kek;
    private int index = 0;
    private int challengePeriod = 5000; //in milliseconds

    /**
     * Creates new form BluetoothServer
     */
    public BluetoothServer() {

        initComponents();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                waitForConnection();
            }
        });
        t1.start();

    }

    private void waitForConnection() {
        // retrieve the local Bluetooth device object
        LocalDevice local = null;

        StreamConnectionNotifier notifier;

        String message;
        // setup the server to listen for connection
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID("8ce255c0200a11e0ac640800200c9a66", false);
            System.out.println(uuid.toString());

            String url = "btspp://localhost:" + uuid.toString() + ";name=BluetoothApp";
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (Exception e) {
            //Upon this if folder is not encrypted, encrypt it
            message = "Bluetooth is not turned on.\n";
            System.err.println(message);
            jTextArea1.setText(message);
            e.printStackTrace();
            return;
        }
        // waiting for connection
        while (true) {
            try {
                message = "waiting for connection...\n";
                jTextArea1.setText(message);
                connection = notifier.acceptAndOpen();
                jTextArea1.setText(jTextArea1.getText() + "Connection established with phone\n.");
                waitForInput();
            } catch (Exception e) {
                message = "Exception thrown while connecting\n .";
                jTextArea1.setText(jTextArea1.getText() + message);
            }
        }
    }

    public void waitForInput() {
        try {
            InputStream inputStream = connection.openInputStream();
            OutputStream outStream = connection.openOutputStream();
            String userName = null;
            String encryptedReplyMessage = null;
            String decryptedClientMessage = null;
            String plainReplyMessage = null;
            MessageProcessor messageProcessor = new MessageProcessor();
            ServerUtils utils = new ServerUtils();
            // boolean isFirstNonce = true;
            while (true) {
                int command = 0;
                int bytes;
                byte[] buffer = new byte[1024];
                inputStream.read(buffer);

                if (buffer[0] != 0) {
                    byte[] filtered = utils.BufferFilter(buffer);//to remove 0 bytes

                    String receivedMsg = new String(filtered);
                    System.out.println("----received message---- " + receivedMsg);

                    if (sessionKey == null) {
                        decryptedClientMessage = receivedMsg;
                    } else {
                        Crypto crypto = new Crypto();
                        decryptedClientMessage = crypto.decrypt(receivedMsg, sessionKey);
                    }

                    //  String processedMsg = messageProcessor.processReceivedString(receivedMsg);
                    String messageType = decryptedClientMessage.split(SEP_PIPE)[0];
                    jTextArea1.setText(jTextArea1.getText() + decryptedClientMessage + "\n");

                    if (index == 0 && messageType.equals(LOGIN_PREFIX)) {
                        //sessionKey=getSessionKey(plainReplyMessage);
                        userName = decryptedClientMessage.split(SEP_PIPE)[1];
                        this.kek = messageProcessor.getKEK(userName);
                         this.sessionKey = messageProcessor.generateSessionKey();
                        plainReplyMessage = messageProcessor.generatePlainMsgWithSession(sessionKey);
                        encryptedReplyMessage = messageProcessor.generateEncryptedMsg(plainReplyMessage, kek);
                        if (encryptedReplyMessage.equals("NOUSER")) {
                            jTextArea1.setText(jTextArea1.getText() + "no user exist with username" + userName + "\n");
                            break;
                        }
                        index++;
                    } else if (index == 1) {
                        //client sending the first nounce, decrypt folder if nounce is correct
                        System.out.println("decrypted client msg is " + decryptedClientMessage);
                        boolean isNonceCorrect = messageProcessor.isNonceCorrect(decryptedClientMessage);
                        if (isNonceCorrect)//This iis because the nonce is wrong.Simply ignore messages
                        {
                            //user can decrypt the KEK.So decrypt the folder for him
                            String folderPath = utils.getFolderPath(userName);
                            utils.decryptFolder(folderPath, kek);
                            jTextArea1.setText(jTextArea1.getText() + "Folder decrypted\n");
                            plainReplyMessage = messageProcessor.generatePlainNounceMessage();
                            encryptedReplyMessage = messageProcessor.generateEncryptedMsg(plainReplyMessage, sessionKey);
                            System.out.println("2nd message from server "+plainReplyMessage);
                        } else {
                            break;
                        }
                        index++;
                    } else {//index==2, to check availability
                        boolean isNonceCorrect = messageProcessor.isNonceCorrect(decryptedClientMessage);
                        
                        if(isNonceCorrect){
                        Thread.sleep(challengePeriod);
                        plainReplyMessage = messageProcessor.generatePlainNounceMessage();
                        encryptedReplyMessage = messageProcessor.generateEncryptedMsg(plainReplyMessage, sessionKey);
                        System.out.println("3rd message from server "+plainReplyMessage);
                        }
                        else{
                            throw  new Exception();
                        }
                    }

                    if (encryptedReplyMessage != null) {
                        outStream.write(encryptedReplyMessage.getBytes());
                    }
                } else {
                    if (userName != null) {
                        sessionKey = null;
                        String folderPath = utils.getFolderPath(userName);
                        utils.encryptFolder(folderPath, kek);
                        System.out.println("need to encrypt folder");
                        jTextArea1.setText(jTextArea1.getText() + "Connection lost\n" + "Folder encrypted\n");
                        break;
                    }
                }
                //processCommand(command);
            }
        } catch (Exception e) {
            System.out.println("ex from waitForInput");
            e.printStackTrace();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Register");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addGap(95, 95, 95))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BluetoothServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BluetoothServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BluetoothServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BluetoothServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        BluetoothServer server = new BluetoothServer();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                server.setVisible(true);
            }
        });
        // server.waitForConnection();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the publicKeyPath
     */
    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    /**
     * @param publicKeyPath the publicKeyPath to set
     */
    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    /**
     * @return the privateKeyPath
     */
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    /**
     * @param privateKeyPath the privateKeyPath to set
     */
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
}
