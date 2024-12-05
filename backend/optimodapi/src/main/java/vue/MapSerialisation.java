/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.Map;

/**
 *
 * @author wockehs
 */
public class MapSerialisation extends Serialisation {

	@Override
	public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map map = (Map) request.getAttribute("map");
		Boolean success = (Boolean) request.getAttribute("success");

		JsonObject container = new JsonObject();
		Gson gson = new Gson();
		String mapJson = gson.toJson(map);
		
		container.addProperty("success", success);
		container.addProperty("map", mapJson);

		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(container.toString());
		out.close();
	}
	
}
