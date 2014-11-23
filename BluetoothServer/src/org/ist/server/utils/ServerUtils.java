/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Chathuri
 */
public class ServerUtils {
  public boolean isPasswordStrong(String password,String regex){
  Pattern pattern=Pattern.compile(regex);
  Matcher matcher=pattern.matcher(password);
  return matcher.matches();
  }  

    public boolean isPasswordEqual(String password, String confirmPassword) {
       return password.equals(confirmPassword);
    }

    public boolean isFileExist(String filePath) {
      File f = new File(filePath);
     return f.exists(); //&& !f.isDirectory();
    }

    public String buildSummaryMessage(boolean passwordStrong, boolean passwordEqual, boolean fileExist) {
      StringBuilder sb=new StringBuilder();
      if(!passwordStrong){
          sb.append("Password should have at least 8 chars, 1 numeric \n1 special charactor and 1 upper and lowercase\n");
         
      }
      if(!passwordEqual){
         sb.append("password and confirm password should be same\n");
      }
      if(!fileExist){
         sb.append("selected file path not exist");
      }
        System.out.println(sb.toString());
      return sb.toString();
    }
}
