/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Crypto {

    public String getEncryptedMessage(String messageToEncrypt, String kek) {
        // We will use the SHA256 algorithm as it has not been compromized yet
        try {
            //For both IV and Key we are using KEK. Better if we can find an alternative to generate good IV
            byte[] keyBytes = getKeyBytes(kek);
            byte[] ivBytes = getIVBytes(kek);
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(ivBytes);
            // initialize the cipher for encrypt mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            // encrypt the message
            byte[] encrypted = cipher.doFinal(messageToEncrypt.getBytes());
            String enc = Base64.encodeToString(encrypted, Base64.DEFAULT);
            System.out.println("Ciphertext: " + enc + "\n");
            String decrypted = decrypt(enc, kek);
            System.out.println("decrypted" + decrypted);
            /*return decrypted;*/
            return enc;
        } catch (Exception ex) {
            System.out
                    .println("Exception thrown while encrypting the initial message");
            ex.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encryptedText, String key)
            throws KeyException, GeneralSecurityException,
            GeneralSecurityException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] keyBytes = getKeyBytes(key);
        byte[] ivBytes = getIVBytes(key);
        return new String(decrypt(cipheredBytes, keyBytes, ivBytes),
                "UTF-8");
    }

    // Get key byte array 128 bit. We need to change this to 256
    private byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes = key.getBytes("UTF-8");
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
                Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }

    // Get Initial Vector byte array 128bit
    private byte[] getIVBytes(String key) throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes = key.getBytes("UTF-8");
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
                Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }

    public byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key,
                "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    //Used AEC-CBC rather than using AES-ECB as patterns exits in the ECB
    public byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,
                "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }

    public void encryptFile(InputStream in, OutputStream out,
            String fileName, String outFile, String KEK) throws Exception {

        byte[] keyBytes = getKeyBytes(KEK);
        byte[] ivBytes = getIVBytes(KEK);
        // System.out.println(">>>>>>>>written"+.encrrays.toString(iv));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // "DES/ECB/PKCS5Padding";"AES/CBC/PKCS5Padding"
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        out = new CipherOutputStream(out, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
        File oldFile = new File(fileName);
        oldFile.delete();
        File newFile = new File(outFile);
        newFile.setReadOnly();
    }

    public void decryptFile(InputStream in, OutputStream out,
            String encriptedFile, String outFile, String KEK) throws Exception {

        byte[] keyBytes = getKeyBytes(KEK);
        byte[] ivBytes = getIVBytes(KEK);
        // System.out.println(">>>>>>>>red"+Arrays.toString(iv));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // "DES/ECB/PKCS5Padding";"AES/CBC/PKCS5Padding"
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        in = new CipherInputStream(in, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
        File f = new File(encriptedFile);
        f.delete();

        File newFile = new File(outFile);
        newFile.setWritable(true);
    }

}
