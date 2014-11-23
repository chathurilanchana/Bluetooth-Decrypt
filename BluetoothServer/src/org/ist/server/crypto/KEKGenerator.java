/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.crypto;

import java.security.MessageDigest;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 * @author Chathuri
 */
public class KEKGenerator {

    private final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // The following constants may be changed without breaking existing hashes.
    private final int SALT_BYTE_SIZE = 24;
    private final int HASH_BYTE_SIZE = 24;
    private final int PBKDF2_ITERATIONS = 1000;

    private final int ITERATION_INDEX = 0;
    private final int SALT_INDEX = 1;
    private final int PBKDF2_INDEX = 2;

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public String createHash(String password)
    {
        String pwdHash = GetSHAHash(password);
        return createHash(password.toCharArray(), pwdHash);
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    // Get SHA-256 hash
    public String GetSHAHash(String text) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] shahash = md.digest();
            hash = convertToHex(shahash);
            System.out.println("HASH - " + hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    // Byte to String conversion
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
                        : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private String createHash(char[] password, String pwdHash) {
        try {
            // Generate salt from pwdHash
            byte[] salt = new byte[SALT_BYTE_SIZE];
            byte[] parameterKeyBytes = pwdHash.getBytes("UTF-8");
            System.arraycopy(parameterKeyBytes, 0, salt, 0,
                    Math.min(parameterKeyBytes.length, salt.length));
            // Hash the password
            byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
            // format iterations:salt:hash
            return toHex(hash);
        } catch (Exception ex) {
            System.out.println("error while generating kek");
            return null;
        }
    }

    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password the password to hash.
     * @param salt the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param hex the hex string
     * @return the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param array the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
}
