package es.us.dad.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import es.us.dad.mysql.entities.Actuator;
import es.us.dad.mysql.entities.ActuatorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ActuatorServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5581936480104414707L;
	Map<Integer, Actuator> actuadorPass;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer id = Integer.valueOf(req.getParameter("idActuator"));
		
		Actuator actuador = actuadorPass.get(id);
		
		String json = new Gson().toJson(actuador);
		
		response(resp, json);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
	    Actuator actuador = gson.fromJson(reader, Actuator.class);
	
		actuadorPass.put(actuador.getIdActuator(), actuador);
		resp.getWriter().println(gson.toJson(actuador));
		resp.setStatus(201);
	};

	public void init() throws ServletException {
		actuadorPass = new HashMap<Integer, Actuator>();
		actuadorPass.put(0, new Actuator("dispositivo 1", 2, ActuatorType.Relay, false));
		actuadorPass.put(1, new Actuator("dispositivo 2", 1, ActuatorType.Fan, false));
		actuadorPass.put(2, new Actuator("dispositivo 3", 5, ActuatorType.Light, false));
		actuadorPass.put(3, new Actuator("dispositivo 4", 8, ActuatorType.Motor, false));
		actuadorPass.put(4, new Actuator("dispositivo 5", 0, ActuatorType.WaterPump, false));
		super.init();
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
		Actuator actuador = gson.fromJson(reader, Actuator.class);
		
		actuadorPass.remove(actuador.getIdActuator());
		
		resp.getWriter().println(gson.toJson(actuador));
		resp.setStatus(201); 
	}

	private void response(HttpServletResponse resp, String msg) throws IOException {
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<t1>" + msg + "</t1>");
		out.println("</body>");
		out.println("</html>");
	}
}
