/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vue;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wockehs
 */
public class MapSerialisation extends Serialisation {

	@Override
	public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonObject container = new JsonObject();
		container.addProperty("success", (Boolean) request.getAttribute("success"));

		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out;
		out = response.getWriter();
		out.println(container);
		out.close();
	}
	
}
