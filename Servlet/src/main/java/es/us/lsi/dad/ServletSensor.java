package es.us.lsi.dad;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Servlet implementation class ServletSensor
 */
public class ServletSensor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private  Sensores sensoresInternos;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public void init() throws ServletException{
		sensoresInternos= Sensores.sensoresRandom(5);
	}
//    public ServletSensor() {
//        super();
//        // TODO Auto-generated constructor stub
//    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		Integer  sensorId = Integer.parseInt(request.getParameter("id"));
		if(sensoresInternos.contieneSensor(sensorId)) {
			String mensaje = sensoresInternos.getSensor(sensorId).toString();
			resp.setStatus(200);
			response(resp, mensaje);
		}else {
			resp.setStatus(404);
			response(resp, "El sensor no existe, estos son los sensores existentes \r\n" + sensoresInternos.toString());
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	/**
	 *
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader reader = req.getReader();
		Gson gson = new Gson();
		Sensor sensor = gson.fromJson(reader,Sensor.class);
		// expandir las condiciones 
		// esto quizas deberia encargarse la clase sensor con los checkers 
		//Boolean cond = sensor.getId()!= null && sensor.getTimestamp()!= null;
		Boolean cond = true;
		if(cond) {
			sensoresInternos.addSensor(sensor);
			resp.setStatus(201);
			resp.getWriter().println(gson.toJson(sensor));
		}else {
			resp.setStatus(300);
			response(resp, "Formato equivocado");
		}
		
		
	}
	protected void doUpdate(HttpServletRequest request, HttpServletResponse response) {
		//TODO
		//para luego, que si no nos morimos
	}
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer  sensorId = Integer.parseInt(req.getParameter("id"));
		if(sensoresInternos.contieneSensor(sensorId)) {
			Boolean res = sensoresInternos.deleteSensor(sensorId);
			String mensaje = "El sensor no se ha eliminado";
			if(res) {
			resp.setStatus(200);
			
			mensaje = "El sensor "+ sensorId.toString()+ "se ha eliminado con exito";
			}
			response(resp, mensaje);
		}else {
			resp.setStatus(404);
			response(resp, "El sensor no existe");
		}
	}
	public void destroy() {
		// do nothing.
	}
	private void response(HttpServletResponse resp, String msg) throws IOException {
		//cádigo del profesor
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<t1>" + msg + "</t1>");
		out.println("</body>");
		out.println("</html>");
	}

}
