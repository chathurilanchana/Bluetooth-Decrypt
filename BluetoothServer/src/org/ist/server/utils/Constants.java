/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

/**
 *
 * @author Chathuri
 */
public class Constants {

    public static final String ERROR_CODE = "ERROR";
    public static final String KEY_FILE_EXTENSION = ".der";
    public static final String LOGIN_PREFIX = "LG";
    public static final String SESSION_PREFIX = "SK";
    public static final String NONSE_STRING = "NS";
    public static final String SEP_MSG = ";";
    public static final String SEP_PIPE = ":";
    public static final String USER_EXIST_CODE = "EXIST";
    public static final String SUCCESS_CODE = "SUCCESS";
    public static final String NO_USER_EXIST = "NOUSER";
    public static final String ENCRYPT_FILE_EXTENSION = ".enc";
    public static final String DECRYPT_FILE_EXTENSIOn = ".dec";
    public static final String passwordRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";//at least min 8 chars, 1 uppercase,one numeric, one special char, 1 lower
}
