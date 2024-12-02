/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author jassirhabba
 */
public class TextFileParser implements FileParser<String> {

    @Override
    public String parse(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath))).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
