/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author jassirhabba
 */
public class FileParserFactory {

    public static FileParser<?> getParser(FileType fileType) {
        switch (fileType) {
            case XmlMap:
                return new XmlMapParser(); // retourne un FileParser<Map>
            case XmlDemande:
                return new XmlDemandeParser(); // retourne un FileParser<Void>
            case Txt:
                return new TextFileParser(); // retourne un FileParser<Void>
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }
}
