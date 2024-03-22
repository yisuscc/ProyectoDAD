package rest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
		// que se encarga de coger las apis
//		Router router = Router.router(vertx);
//		vertx.createHttpServer().requestHandler(router::handle).listen(8069,result->{
//			if(result.succeeded()) {
//				startFuture.complete();
//			}else {
//				startFuture.fail("El lanzamiento del servidor ha fallado"+result.cause());
//				
//			}
//		});
		vertx.createHttpServer().requestHandler(r -> {
			r.response().end("<h1>Bienvenido a mi primera aplicacion Vert.x 3</h1>"
					+ "Esto es un ejemplo de una Verticle sencillo para probar el despliegue");
		}).listen(8089, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
		// Asociamos las funciones a una api
		// Estructura:
		/*
		 * router.post("/api/users").handler(this::addOne);
		 * router.route("/api/users*").handler(BodyHandler.create());
		 * router.get("/api/users").handler(this::getAllWithParams);
		 * router.get("/api/users/user/all").handler(this::getAll);
		 */

		//router.route("/api/*").handler(BodyHandler.create());
		// DADOS UNA Placa y un id de sensor o actuador
		// TODO 1primera api añade una medición
		// TODO 2 devuelve la ultima medición
		// TODO 3 devuelve el estado de un actuador;
		// TODO 4 añade el estado de un actuador;
		// Dada una placa id
		// TODO 5 lo mismo pero devolviendo el ultimo estado de todos los actuadores o
		// sensores;
		// OPcionales que no te ocupen mucho tiempo
		// lo mismo que las 1, 2,3 ,4,5, pero que te de las 5 mas recientes
		//

	}

	// creamos el stop
	public void stop(Promise<Void> stopPromise) throws Exception {
		try {
			// TODO LIMPIAR la claseAglutinador.
			stopPromise.complete();
		} catch (Exception e) {
			stopPromise.fail(e);
		}
		super.stop(stopPromise);
	}

}
