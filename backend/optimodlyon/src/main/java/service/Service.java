/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

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
    
    public Map loadMap(String fileName) {

        // Déterminer le type de fichier
        FileType fileType = FileParserFactory.determineFileType(fileName);

        // Vérifier si le type de fichier est bien XmlMap
        if (fileType != FileType.XMLMAP) {
            throw new IllegalArgumentException("Invalid file type for loading a map: " + fileType);
        }

        // Récupérer le parser approprié via la factory
        FileParser<HashMap<Long, Intersection>> parser = (FileParser<HashMap<Long, Intersection>>) FileParserFactory.getParser(fileType);

        // Parse le fichier et récupère la HashMap d'intersections
        HashMap<Long, Intersection> intersectionMap = parser.parse(fileName);

        // Construire l'objet Map à partir de la HashMap d'intersections
        Map map = new Map(intersectionMap);

        return map;
    }


}
