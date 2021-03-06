/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import org.ist.server.crypto.AssymetricEncryptionHandler;
import org.ist.server.crypto.Crypto;

public class ServerUtils {

    public boolean isPasswordStrong(String password) {
        Pattern pattern = Pattern.compile(Constants.passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isPasswordEqual(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public boolean isFileExist(String filePath) {
        File f = new File(filePath);
        return f.isDirectory();
    }

    public boolean isFolderAlreadyEncrypted(String folderPath) {
        try{
        File dir = new File(folderPath);
        File[] directoryListing = dir.listFiles();
           // String cmd = "attrib +h "+fileName; //"attrib +h E:\\input"; 
        // Runtime.getRuntime().exec(cmd);
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String childName = child.getAbsolutePath();
                if (childName.endsWith(Constants.ENCRYPT_FILE_EXTENSION)) {
                return true;
                }
            }
        }
        }
        catch(Exception ex){
            return false;
        }
        return false;
    }

    public String generateFolderEncryptionKey() {
        return UUID.randomUUID().toString();
    }

    public String buildSummaryMessage(boolean passwordStrong, boolean passwordEqual, boolean fileExist,boolean alreadyEncrypted) {
        StringBuilder sb = new StringBuilder();
        if (!passwordStrong) {
            sb.append("Password should have at least 8 chars, 1 numeric \n1 special charactor and 1 upper and lowercase\n");

        }
        if (!passwordEqual) {
            sb.append("password and confirm password should be same\n");
        }
        if (!fileExist) {
            sb.append("selected file path not exist\n");
        }
        if(alreadyEncrypted){
            sb.append("Selected folder is already encrypted");
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public byte[] BufferFilter(byte[] packet) {
        int i = packet.length - 1;
        while (packet[i] == 0) {
            --i;
        }
        byte[] temp = new byte[i + 1];
        System.arraycopy(packet, 0, temp, 0,
                i + 1);
        return temp;
    }

    public void copyFile(int mode, String inputFile, String outputFile,
            String password) throws Exception {

        BufferedInputStream is = new BufferedInputStream(new FileInputStream(
                inputFile));
        BufferedOutputStream os = new BufferedOutputStream(
                new FileOutputStream(outputFile));
        Crypto crypto = new Crypto();
        if (mode == Cipher.ENCRYPT_MODE) {
            crypto.encryptFile(is, os, inputFile, outputFile, password);
        } else if (mode == Cipher.DECRYPT_MODE) {
            crypto.decryptFile(is, os, inputFile, outputFile, password);
        }
        is.close();
        os.close();
        File f = new File(inputFile);
        f.delete();
    }

    public String getFolderPath(String username, String privateKey) {
        AssymetricEncryptionHandler assymHandler = new AssymetricEncryptionHandler();
        String folderPath = new DBConnector().retrieveFolderPath(username);
        String folderDecryptedByAdminKey = assymHandler.decryptByPrivateKey(folderPath, privateKey);
        return folderDecryptedByAdminKey;
    }

    public void decryptFolder(String fileName, String key) {
        try {
            File dir = new File(fileName);
            File[] directoryListing = dir.listFiles();
           // String cmd = "attrib -h "+fileName; //"attrib +h E:\\input"
            // Runtime.getRuntime().exec(cmd);
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String childName = child.getAbsolutePath();
                    StringBuilder sb = new StringBuilder();
                    sb.append(childName);
                    if (childName.endsWith(Constants.ENCRYPT_FILE_EXTENSION)) {
                        sb.replace(childName.lastIndexOf("."), childName.length(), "");
                        String decryptedName = sb.toString();

                        copyFile(Cipher.DECRYPT_MODE, childName, decryptedName,
                                key);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("error while encrypting the files");
        }

    }

    public void encryptFolder(String fileName, String key) {
        try {
            File dir = new File(fileName);
            File[] directoryListing = dir.listFiles();
           // String cmd = "attrib +h "+fileName; //"attrib +h E:\\input"; 
            // Runtime.getRuntime().exec(cmd);
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String childName = child.getAbsolutePath();
                    if (!childName.endsWith(Constants.ENCRYPT_FILE_EXTENSION)) {
                        String encryptedName = childName + Constants.ENCRYPT_FILE_EXTENSION;

                        copyFile(Cipher.ENCRYPT_MODE, childName, encryptedName,
                                key);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("error while encrypting the files");
        }
    }

}
