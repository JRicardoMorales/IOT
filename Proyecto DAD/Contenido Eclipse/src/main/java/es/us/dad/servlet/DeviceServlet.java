package es.us.dad.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import es.us.dad.mysql.entities.Device;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeviceServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5581936480104414707L;
	Map<Integer, Device> placaPass;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer id = Integer.valueOf(req.getParameter("idDevice"));
		
		Device placa = placaPass.get(id);
		
		String json = new Gson().toJson(placa);
		
		response(resp, json);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
	    Device placa = gson.fromJson(reader, Device.class);
	
		placaPass.put(placa.getIdDevice(), placa);
		resp.getWriter().println(gson.toJson(placa));
		resp.setStatus(201);
	};

	public void init() throws ServletException {
		placaPass = new HashMap<Integer, Device>();
		Long initNumber = Integer.toUnsignedLong(0);
		Long initNumber2 = Integer.toUnsignedLong(1);
		placaPass.put(0, new Device("1", "Dispositivo 1", "Canal 1", initNumber, initNumber2, Integer.valueOf(3)));
		placaPass.put(1, new Device("2", "Dispositivo 2", "Canal 2", initNumber, initNumber2, Integer.valueOf(4)));
		placaPass.put(2, new Device("3", "Dispositivo 3", "Canal 5", initNumber, initNumber2, Integer.valueOf(5)));
		placaPass.put(3, new Device("4", "Dispositivo 4", "Canal 8", initNumber, initNumber2, Integer.valueOf(6)));
		placaPass.put(4, new Device("5", "Dispositivo 5", "Canal 6", initNumber, initNumber2, Integer.valueOf(7)));
		super.init();
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
	    Device placa = gson.fromJson(reader, Device.class);
		
		placaPass.remove(placa.getIdDevice());
		
		resp.getWriter().println(gson.toJson(placa));
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

