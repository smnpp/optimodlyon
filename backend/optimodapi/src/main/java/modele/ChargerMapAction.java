/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import javax.servlet.http.HttpServletRequest;
import service.Service;

/**
 *
 * @author Hazim Asri
 */
public class ChargerMapAction extends Action {
	
	public ChargerMapAction(Service service) {
		super(service);
	}

	@Override
	public void execute(HttpServletRequest request) {
		String file = request.getParameter("file");
		System.out.println(file);
		
		
		service.loadMap(file);
	}
	
}
