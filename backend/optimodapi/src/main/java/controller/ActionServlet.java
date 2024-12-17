package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.Map;
import modele.ChargerMapAction;
import modele.ChargerTourAction;
import modele.ComputeTourAction;
import modele.LoadRequestAction;
import modele.SaveTourAction;
import modele.ComputeMultipleTourAction;
import service.Service;
import vue.MapSerialisation;
import vue.TourRequestSerialisation;
import vue.TourSerialisation;
import vue.SuccessSerialisation;
import vue.MultipleTourSerialisation;
import vue.TourRestoreSerialialisation;

/**
 *
 * @author wockehs
 */
@WebServlet(name = "ActionServlet", urlPatterns = {"/ActionServlet"})
public class ActionServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");

        Service service = new Service();
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "load-map": {
                    new ChargerMapAction(service).execute(request);
                    new MapSerialisation().appliquer(request, response);
                    break;
                }
                case "load-request": {
                    new LoadRequestAction(service).execute(request);
                    new TourRequestSerialisation().appliquer(request, response);
                    break;
                }
                case "compute-tour": {
                    new ComputeTourAction(service).execute(request);
                    new TourSerialisation().appliquer(request, response);
                    break;
                }
                case "compute-n-tour": {
                    new ComputeMultipleTourAction(service).execute(request);
                    new MultipleTourSerialisation().appliquer(request, response);
                    break;
                }
                case "save-tour": {
                    new SaveTourAction(service).execute(request);
                    new SuccessSerialisation().appliquer(request, response);
                    break;
                }
                case "restore-tour": {
                    new ChargerTourAction(service).execute(request);
                    new TourRestoreSerialialisation().appliquer(request, response);
                    break;
                }
                case "test": {

                    JsonObject container = new JsonObject();

                    container.addProperty("success", true);

                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println(container.toString());
                    out.close();
                    break;
                }
                default:
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
