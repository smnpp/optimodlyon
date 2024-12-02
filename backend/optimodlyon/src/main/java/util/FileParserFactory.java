/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author jassirhabba
 */
public class FileParserFactory {

    public static FileParser<?> getParser(FileType fileType) {
        switch (fileType) {
            case XMLMAP:
                return new XmlMapParser(); // retourne un FileParser<Map>
            case XMLDEMANDE:
                return new XmlDemandeParser(); // retourne un FileParser<Void>
            case TXT:
                return new TextFileParser(); // retourne un FileParser<Void>
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }
    
        public static FileType determineFileType(String fileName) {
        if (fileName.endsWith(".txt")) {
            return FileType.TXT;
        } else if (fileName.endsWith(".xml")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                // Parcours des premières lignes pour trouver une balise identifiable
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // Ignorer les lignes XML d'entête ou vides
                    if (line.isEmpty() || line.startsWith("<?xml")) {
                        continue;
                    }

                    // Identifier le type de fichier à partir de la balise principale
                    if (line.contains("<reseau")) {
                        return FileType.XMLMAP;
                    } else if (line.contains("<demandeDeLivraisons")) {
                        return FileType.XMLDEMANDE;
                    } else {
                        throw new IllegalArgumentException("Unsupported XML format: " + fileName);
                    }
                }

                // Si aucune balise identifiable n'est trouvée
                throw new IllegalArgumentException("Invalid or empty XML file: " + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Error reading file: " + fileName, e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported file extension for file: " + fileName);
        }
    }

}
