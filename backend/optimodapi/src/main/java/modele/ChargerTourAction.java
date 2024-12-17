/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import metier.Map;
import service.Service;

/**
 *
 * @author jassirhabba
 */
public class ChargerTourAction extends Action {

    public ChargerTourAction(Service service) {
        super(service);
    }

    @Override

    public void execute(HttpServletRequest request) {
        BufferedReader reader = null;
        try {
            reader = request.getReader();
        } catch (IOException ex) {
            Logger.getLogger(ChargerMapAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        Gson gson = new Gson();
        JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
        String fileContent = jsonRequest.get("file-content").getAsString();

        if (fileContent != null) {
            try {
                JsonObject tour = service.restoreTour(fileContent);

                request.setAttribute("success", true);
                request.setAttribute("tour", tour);
            } catch (IOException ex) {
                Logger.getLogger(ChargerMapAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            request.setAttribute("success", false);
        }
    }

}
