/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ist.server.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Chathuri
 */
public class KeyFileFilter extends FileFilter implements java.io.FileFilter {

    @Override
    public boolean accept(File f) {
      if (f.getName().toLowerCase().endsWith(Constants.KEY_FILE_EXTENSION.toLowerCase())){
          return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      return "DER FILES";
    }
    
}
