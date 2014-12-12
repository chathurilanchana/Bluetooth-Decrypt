
package org.ist.server.crypto;

import java.io.*;
import java.net.URISyntaxException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.ist.server.utils.Constants;

public class AssymetricEncryptionHandler {

    public final String ALGORITHM = "RSA";
    public final String ENCODING = "UTF8";

    public String encryptByPublicKey(String text, String adminPublicKeyPath) {
        try {
            File f = new File(adminPublicKeyPath);
            if (!f.exists()) {
                return Constants.ERROR_CODE;
            }
            PublicKey pk = getPublicKey(adminPublicKeyPath);
            byte[] encrypted = encrypt(text, pk);
            String enc = Base64.encodeToString(encrypted, Base64.DEFAULT);
            System.out.println("Encrypted is " + enc);
            return enc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Constants.ERROR_CODE;
        }
    }

    public String decryptByPrivateKey(String encryptedText, String adminPrivateKeyPath) {
        try{
              File f = new File(adminPrivateKeyPath);
            if (!f.exists()) {
                return Constants.ERROR_CODE;
            }
        PrivateKey pv = getPrivateKey(adminPrivateKeyPath);
        byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decrypted = decrypt(cipheredBytes, pv);
        String decryptedTxt = new String(decrypted, "UTF-8");
        return decryptedTxt;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return Constants.ERROR_CODE;
        }
    }

    private PublicKey getPublicKey(String publicKeyUrl) throws URISyntaxException, FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = getKeyBytes(publicKeyUrl);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePublic(spec);
    }

    private PrivateKey getPrivateKey(String privateKeyUrl) throws Exception {
        byte[] keyBytes = getKeyBytes(privateKeyUrl);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePrivate(spec);

        /* KeyStore ks = KeyStore.getInstance("AES");
         ks.load(new FileInputStream(privateKeyUrl), "chathuri".toCharArray());
         String alias = (String) ks.aliases().nextElement();
         KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection("chathuri".toCharArray()));
         return keyEntry.getPrivateKey();*/
    }

    private byte[] encrypt(String input, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, UnsupportedEncodingException {
        byte[] cipherText;
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipherText = cipher.doFinal(input.getBytes(ENCODING));
        return cipherText;
    }

    private byte[] decrypt(byte[] input, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
        byte[] cipherText;
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        cipherText = cipher.doFinal(input);
        return cipherText;
    }

    private byte[] getKeyBytes(String url) throws URISyntaxException, FileNotFoundException, IOException {
        File f = new File(url);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        System.out.println(keyBytes.length);
        dis.readFully(keyBytes);
        dis.close();
        return keyBytes;
    }
}
