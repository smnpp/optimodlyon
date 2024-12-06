/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import metier.DeliveryRequest;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;

import metier.TourRequest;
import metier.Warehouse;
/**
 *
 * @author simonperret
 */
public class XmlDemandeParser implements FileParser<TourRequest> {

    @Override
    public TourRequest parse(File file) {
        TourRequest tourRequest = null;
        try {
            java.util.Map<String, DeliveryRequest> requests = new HashMap<>();
            Warehouse warehouse = null;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList entrepots = document.getElementsByTagName("entrepot");

            for (int i = 0; i < entrepots.getLength(); i++) {
                Element entrepot = (Element) entrepots.item(i);
                Long address = Long.parseLong(entrepot.getAttribute("adresse"));

                 DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE)
                .optionalEnd()
                .toFormatter();

                LocalTime localTime = LocalTime.parse(entrepot.getAttribute("heureDepart"), formatter);
                warehouse = new Warehouse(address, localTime);

            }

            NodeList livraisons = document.getElementsByTagName("livraison");

            for (int i = 0; i < livraisons.getLength(); i++) {
                Element livraison = (Element) livraisons.item(i);
                Long pickupPoint = Long.parseLong(livraison.getAttribute("adresseEnlevement"));
                Long deliveryPoint = Long.parseLong(livraison.getAttribute("adresseLivraison"));

                Duration pickupDuration = Duration.ofSeconds(Long.parseLong(livraison.getAttribute("dureeEnlevement")));
                Duration deliveryDuration = Duration.ofSeconds(Long.parseLong(livraison.getAttribute("dureeLivraison")));

                DeliveryRequest deliveryRequest = new DeliveryRequest(pickupPoint, deliveryPoint, pickupDuration, deliveryDuration);
                requests.put(deliveryRequest.getId(), deliveryRequest);
            }

            tourRequest = new TourRequest(requests, warehouse);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tourRequest;
    }
}