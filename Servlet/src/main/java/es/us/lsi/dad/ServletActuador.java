package es.us.lsi.dad;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

/**
 * Servlet implementation class ServletActuador
 */
public class ServletActuador extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Actuadores actuadoresInternos;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletActuador() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init() throws ServletException{
    	actuadoresInternos = Actuadores.actuadoresRandom(5);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
	Integer actuadorId = Integer.parseInt(request.getParameter("id"));
	if(actuadoresInternos.contieneActuador(actuadorId)) {
		String mensaje = actuadoresInternos.getActuador(actuadorId).toString();
		resp.setStatus(200);
		response(resp, mensaje);
	}else {
		resp.setStatus(404);
		response(resp, "El actuador no existe, estos son los actuadores existentes \r\n"+actuadoresInternos.toString());

	}
		
	}
	protected void doUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			//TODO Para Luego
		
	}
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer  actuadorId = Integer.parseInt(req.getParameter("id"));
		if(actuadoresInternos.contieneActuador(actuadorId)) {
			String mensaje = "El actuador no se ha eliminado";
		Boolean res = actuadoresInternos.deleteActuador(actuadorId);
		if(res) {
			resp.setStatus(200);
			mensaje = "El actuador" + actuadoresInternos.getActuador(actuadorId).toString()+ "se ha eliminado con exito";
		}
		response(resp, mensaje);
		}else {
			resp.setStatus(404);
			response(resp, "El sensor no existe");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();
		Actuador actuador = gson.fromJson(reader,Actuador.class);
		// no se que condiciones ponerle 
		Boolean cond = true;
		if(cond) {
			actuadoresInternos.addActuador(actuador);
			resp.setStatus(201);
			resp.getWriter().println(gson.toJson(actuador));
			
		}else {
			resp.setStatus(300);
			response(resp, "Formato equivocado");
		}
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
	public void destroy() {
		// do nothing.
	}

}
