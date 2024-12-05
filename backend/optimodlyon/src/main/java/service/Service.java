/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import metier.Intersection;
import metier.Map;
import util.FileParser;
import util.FileParserFactory;
import util.FileType;

/**
 *
 * @author jnoukam
 */
public class Service {
    
	public Map loadMap(String fileContent, String fileName) throws IOException {

		// Déterminer le type de fichier
		File file = File.createTempFile("temp", ".xml");
		file.deleteOnExit();
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(fileContent);
		}
		
		FileType fileType = FileParserFactory.determineFileType(file);
		// Vérifier si le type de fichier est bien XmlMap
		if (fileType != FileType.XMLMAP) {
			throw new IllegalArgumentException("Invalid file type for loading a map: " + fileType);
		}

		// Récupérer le parser approprié via la factory
		FileParser<HashMap<Long, Intersection>> parser = (FileParser<HashMap<Long, Intersection>>) FileParserFactory.getParser(fileType);

		// Parse le fichier et récupère la HashMap d'intersections
		HashMap<Long, Intersection> intersectionMap = parser.parse(file);

		// Construire l'objet Map à partir de la HashMap d'intersections
		Map map = new Map(intersectionMap);

		return map;
	}


}
