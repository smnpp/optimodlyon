/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test;

import util.FileParser;
import util.FileParserFactory;
import metier.Map;
import util.FileType;
import static util.FileType.XmlMap;
import util.TextFileParser;
import util.XmlDemandeParser;
import util.XmlMapParser;

/**
 *
 * @author jassirhabba
 */
public class main {

    public static void main(String[] args) {

        String filePath = "src/main/java/test/petitPlan.xml";
        FileType fileType = XmlMap; // enum
        try {
            // Obtenir le bon parseur
            FileParser<?> parser = FileParserFactory.getParser(fileType);

            // Utiliser le parseur en fonction du type attendu
            if (parser instanceof XmlMapParser) {
                Map map = ((XmlMapParser) parser).parse(filePath);

                System.out.println("Map parsed successfully: " + map);
            } else if (parser instanceof XmlDemandeParser) {
                ((XmlDemandeParser) parser).parse(filePath);
                System.out.println("Demande parsed successfully");
            } else if (parser instanceof TextFileParser) {
                ((TextFileParser) parser).parse(filePath);
                System.out.println("Text file parsed successfully");
            } else {
                System.out.println("Unknown parser type");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
