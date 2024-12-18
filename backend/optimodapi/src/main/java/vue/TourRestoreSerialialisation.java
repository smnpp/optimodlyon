/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.Intersection;
import metier.Tour;

/**
 *
 * @author jassirhabba
 */
public class TourRestoreSerialialisation extends Serialisation {

    @Override
    public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject tour = (JsonObject) request.getAttribute("tour");
        Boolean success = (Boolean) request.getAttribute("success");

        JsonObject container = new JsonObject();

        container.addProperty("success", success);
        container.add("tour", tour);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(container.toString());
        out.close();

    }

}
