
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author jassirhabba
 */
public class FileParserFactory {

    /**
     * Returns the appropriate FileParser based on the provided file type.
     *
     * @param fileType The type of the file for which a parser is needed. 
     *                 It can be one of the following: XMLMAP, XMLDEMANDE, or TXT.
     * @return A specific FileParser implementation depending on the file type:
     *         - {@link XmlMapParser} for XMLMAP files (parses Map).
     *         - {@link XmlDemandeParser} for XMLDEMANDE files (parses Void).
     *         - {@link TextFileParser} for TXT files (parses Void).
     * @throws IllegalArgumentException If the provided file type is unsupported.
     */
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


    /**
    * Determines the file type of the given file by inspecting its content.
    * This method reads the file's first few lines to identify the main XML tag, 
    * which helps determine if the file is of type XMLMAP, XMLDEMANDE, or an unsupported format.
    *
    * @param file The file whose type is to be determined.
    * @return The corresponding {@link FileType} based on the XML structure:
    *         - {@link FileType#XMLMAP} if the file contains a &lt;reseau&gt; tag.
    *         - {@link FileType#XMLDEMANDE} if the file contains a &lt;demandeDeLivraisons&gt; tag.
    * @throws IllegalArgumentException If the XML file format is unsupported or invalid.
    * @throws RuntimeException If an error occurs while reading the file.
    */
        public static FileType determineFileType(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
					throw new IllegalArgumentException("Unsupported XML format ");
				}
			}

			// Si aucune balise identifiable n'est trouvée
			throw new IllegalArgumentException("Invalid or empty XML file ");
		} catch (IOException e) {
			throw new RuntimeException("Error reading file ", e);
		}
	}
}


