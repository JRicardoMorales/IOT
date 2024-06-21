package es.us.dad.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import es.us.dad.mysql.entities.Group;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GroupServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5581936480104414707L;
	Map<String, Group> groupPass;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String group = req.getParameter("name");
		
        Group grupo = groupPass.get(group);
		
		String json = new Gson().toJson(grupo);
		
		response(resp, json);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
	    Group group = gson.fromJson(reader, Group.class);
	
	    groupPass.put(group.getName(), group);
		resp.getWriter().println(gson.toJson(group));
		resp.setStatus(201);
	};

	public void init() throws ServletException {
		groupPass = new HashMap<String, Group>();
		Long initNumber = Integer.toUnsignedLong(0);
		groupPass.put("Dispositivo 1", new Group("Canal 1", "Dispositivo 1", initNumber));
		groupPass.put("Dispositivo 2", new Group("Canal 6", "Dispositivo 2", initNumber));
		groupPass.put("Dispositivo 3", new Group("Canal 9", "Dispositivo 3", initNumber));
		groupPass.put("Dispositivo 4", new Group("Canal 4", "Dispositivo 4", initNumber));
		groupPass.put("Dispositivo 5", new Group("Canal 3", "Dispositivo 5", initNumber));
		super.init();
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
	    Group group = gson.fromJson(reader, Group.class);
		
	    groupPass.remove(group.getName());
		
		resp.getWriter().println(gson.toJson(group));
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
