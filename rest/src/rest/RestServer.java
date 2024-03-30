package rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServer extends AbstractVerticle {
	private Gson gson;
	private AglutinadorPlacaSensorActuador apsa;

	public void start(Promise<Void> startFuture) {
		// TODO creamos datos sinteticos
		apsa = AglutinadorPlacaSensorActuador.getRandomData(5);
		// COnfiguramos los datos del gson;
		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // ISO-8601 FTW
		// Definimos el router
		// que se encarga de coger las apis y redirigirlas
	Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8069,result->{			if(result.succeeded()) {
			startFuture.complete();
			}else {
			startFuture.fail("El lanzamiento del servidor ha fallado"+result.cause());
							}
		});
	
		// Asociamos las funciones a una api
		// A modo de orientació, esta es la extructura:
		/*
		 * router.post("/api/users").handler(this::addOne);
		 * router.route("/api/users*").handler(BodyHandler.create());
		 * router.get("/api/users").handler(this::getAllWithParams);
		 * router.get("/api/users/user/all").handler(this::getAll);
		 */

		router.route("/api/*").handler(BodyHandler.create());
		// DADOS UNA Placa y un id de sensor o actuador
		// TODO 1primera api añade una medición
		router.post("/api/sensor").handler(this::setSensor);
		// TODO 2 devuelve la ultima medición
		router.get("/api/sensor/:placaId/:id").handler(this::getSensor);
		// TODO 3 devuelve el ultimo  estado de un actuador;
		router.get("/api/actuador/:placaId/:id").handler(this::getActuador);
		// TODO 4 añade el estado de un actuador;
		router.post("/api/actuador").handler(this::setActuador);
		// Dada una placa id
		// TODO 5 lo mismo pero devolviendo el ultimo estado de todos los actuadores o
		// sensores de una placa dada;
		router.post("/api/sensores/:placaId").handler(this::getAllSensores);
		router.post("/api/actuadores/:placaId").handler(this::getAllActuadores);
		// Opcionales que no te ocupen mucho tiempo
		// lo mismo que las , 2,3 ,5, pero que te de las x mas recientes
		//

	}
	// definimos las llamadas del handler 
	private void setSensor(RoutingContext routingContext) {
//	TODO 	
	}
	private void setActuador(RoutingContext routingContext) {
		//TODO
	}
private void getSensor(RoutingContext routingContext) {
	//TODO
}
private void getActuador(RoutingContext routingContext) {
	//TODO
}
private void getAllSensores(RoutingContext routingContext) {
	final Integer placaId = routingContext.queryParams().contains("placaId")?Integer.parseInt(routingContext.queryParam("placaId").get(0)):null;
	List<Sensor> lsAux = placaId!= null && apsa.existePlaca(placaId)?apsa.getLastSensoresList(placaId):  new ArrayList<Sensor>();
	routingContext.response().
	putHeader("content-type", "application/json; charset=utf-8").
	setStatusCode(200).end(gson.toJson(lsAux));
}
private void getAllActuadores(RoutingContext routingContext) {
	final Integer placaId = routingContext.queryParams().contains("placaId")?Integer.parseInt(routingContext.queryParam("placaId").get(0)):null;
	List<Actuador> lsAux = placaId!= null && apsa.existePlaca(placaId)?apsa.getLastActuadoresList(placaId):  new ArrayList<Actuador>();
	routingContext.response().
	putHeader("content-type", "application/json; charset=utf-8").
	setStatusCode(200).end(gson.toJson(lsAux));
}
	// creamos el stop
	public void stop(Promise<Void> stopPromise) throws Exception {
		try {
			apsa.clear();
			stopPromise.complete();
		} catch (Exception e) {
			stopPromise.fail(e);
		}
		super.stop(stopPromise);
	}

}
