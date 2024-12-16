/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author jassirhabba
 */
public class TextFileParser implements FileParser<String> {
    
    /**
    * Parses the given file. This method currently throws an exception to indicate 
    * that the operation is not supported.
    *
    * @param file The file to be parsed.
    * @return null, as the operation is not supported and no parsing is performed.
    * @throws UnsupportedOperationException if this method is called, as the operation is unsupported.
    */
    @Override
    public String parse(File file) {
            try {
                    throw new UnsupportedOperationException("Unsupported Operation");
            } catch (Exception e) {
                    e.printStackTrace();
                    return null;
            }
    }
}
