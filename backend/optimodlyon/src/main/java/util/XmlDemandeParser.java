
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

            boolean isFrench = document.getDocumentElement().getTagName().equals("demandeDeLivraisons");
            boolean isEnglish = document.getDocumentElement().getTagName().equals("planningRequest");

            if (!isFrench && !isEnglish) {
                throw new IllegalArgumentException("Unrecognized XML file: Unexpected root element.");
            }

            NodeList depots = isFrench ? document.getElementsByTagName("entrepot")
                                        : document.getElementsByTagName("depot");


            for (int i = 0; i < depots.getLength(); i++) {
                Element entrepot = (Element) depots.item(i);
                Long address = isFrench ? Long.parseLong(entrepot.getAttribute("adresse")) : Long.parseLong(entrepot.getAttribute("address"));

                 DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE)
                .optionalEnd()
                .toFormatter();

                LocalTime localTime = isFrench ? LocalTime.parse(entrepot.getAttribute("heureDepart"), formatter) : LocalTime.parse(entrepot.getAttribute("departureTime"), formatter);
                warehouse = new Warehouse(address, localTime);

            }

            NodeList livraisons = isFrench ? document.getElementsByTagName("livraison") : document.getElementsByTagName("request");

            for (int i = 0; i < livraisons.getLength(); i++) {
                Element livraison = (Element) livraisons.item(i);
                Long pickupPoint = isFrench ? Long.parseLong(livraison.getAttribute("adresseEnlevement")) : Long.parseLong(livraison.getAttribute("pickupAddress"));
                Long deliveryPoint = isFrench ? Long.parseLong(livraison.getAttribute("adresseLivraison")) : Long.parseLong(livraison.getAttribute("deliveryAddress"));

                Duration pickupDuration = isFrench ? Duration.ofSeconds(Long.parseLong(livraison.getAttribute("dureeEnlevement"))) : Duration.ofSeconds(Long.parseLong(livraison.getAttribute("pickupDuration")));
                Duration deliveryDuration = isFrench ? Duration.ofSeconds(Long.parseLong(livraison.getAttribute("dureeLivraison"))): Duration.ofSeconds(Long.parseLong(livraison.getAttribute("deliveryDuration")));

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
