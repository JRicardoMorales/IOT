package es.us.dad.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import es.us.dad.mysql.entities.Sensor;
import es.us.dad.mysql.entities.SensorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SensorServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5581936480104414707L;
	Map<Integer, Sensor> sensorPass;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer id = Integer.valueOf(req.getParameter("idSensor"));
		
		Sensor sensor = sensorPass.get(id);
		
		String json = new Gson().toJson(sensor);
		
		response(resp, json);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
	    Sensor sensor = gson.fromJson(reader, Sensor.class);
	
		sensorPass.put(sensor.getIdSensor(), sensor);
		resp.getWriter().println(gson.toJson(sensor));
		resp.setStatus(201);
	};

	public void init() throws ServletException {
		sensorPass = new HashMap<Integer, Sensor>();
		sensorPass.put(0, new Sensor("Dispositivo 1", 1, SensorType.AirQuality, false));
		sensorPass.put(1, new Sensor("Dispositivo 2", 5, SensorType.Humidity, false));
		sensorPass.put(2, new Sensor("Dispositivo 3", 3, SensorType.Pressure, false));
		sensorPass.put(3, new Sensor("Dispositivo 4", 2, SensorType.Power, false));
		sensorPass.put(4, new Sensor("Dispositivo 5", 7, SensorType.Temperature, false));
		super.init();
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
		Sensor sensor = gson.fromJson(reader, Sensor.class);
		
		sensorPass.remove(sensor.getIdSensor());
		
		resp.getWriter().println(gson.toJson(sensor));
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