package vue;


import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jassirhabba
 */
public class SuccessSerialisation extends Serialisation {

    @Override
    public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Boolean success = (Boolean) request.getAttribute("success");
        JsonObject container = new JsonObject();
        container.addProperty("success", success);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(container.toString());
        out.close();
    }


}