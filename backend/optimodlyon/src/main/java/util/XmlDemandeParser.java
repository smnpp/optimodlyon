/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.File;

/**
 *
 * @author jassirhabba
 */
public class XmlDemandeParser implements FileParser<Void> {

    @Override
    public Void parse(File file) {
        System.out.println("Parsing XML Demande from file: " + file);
        // Logique pour parser le fichier demande
        return null;
    }
}
