package rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

public class RestServer extends AbstractVerticle {
	private Gson gson;
	private AglutinadorPlacaSensorActuador apsa;
	private MySQLPool msc;

	public void start(Promise<Void> startFuture) {
		// creamos datos sinteticos
		//apsa = AglutinadorPlacaSensorActuador.getRandomData(5);
		//System.out.println(apsa.toString());
		// COnfiguramos los datos del gson;
		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // ISO-8601 FTW

		// Conexíon con la base de datos.
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("proyecto_dad").setUser("root").setPassword("root");
		// mediante la MYSQLConnectOptions.
		PoolOptions poolOptions = new PoolOptions().setMaxSize(37); // por poner un numero
		msc = MySQLPool.pool(vertx, connectOptions, poolOptions);
		
		// Nota:el usuario y contrasena de root es root
		// Alternativamente PDAD es PDAD.

		// creamos datos sintéticos
		generaRandomData();
		// Definimos el router
		// que se encarga de coger las apis y redirigirlas
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8060, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail("El lanzamiento del servidor ha fallado" + result.cause());
			}
		});

		// Asociamos las funciones a una api

		router.route("/api/*").handler(BodyHandler.create());
		// DADOS UNA Placa y un id de sensor o actuador
		// 1primera api añade una medición
		router.post("/api/sensor").handler(this::setSensor);
		// 2 devuelve la ultima medición
		router.get("/api/sensor/:placaId/:id").handler(this::getSensor);
		// 3 devuelve el ultimo estado de un actuador;
		router.get("/api/actuador/:placaId/:id").handler(this::getActuador);
		// 4 añade el estado de un actuador;
		router.post("/api/actuador").handler(this::setActuador);
		// Dada una placa id
		// 5 lo mismo pero devolviendo el ultimo estado de todos los actuadores o
		// sensores de una placa dada;
		router.get("/api/sensores/:placaId").handler(this::getAllSensores);
		router.get("/api/actuadores/:placaId").handler(this::getAllActuadores);
		// Opcionales que no te ocupen mucho tiempo
		// lo mismo que las , 2,3 ,5, pero que te de las x mas recientes
		// TODO: hacer una que de la última medición a partir de una hora dada?
		// TODO: hacer que devuelv todo los valores del os sensores y actuadores de
		// unmismo group id
	}

	// definimos las llamadas del handler
	private void setSensor(RoutingContext routingContext) {
		final Medicion medicion = gson.fromJson(routingContext.getBodyAsString(), Medicion.class);
		apsa.addSensor(medicion);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(medicion));
	}

	private void setActuador(RoutingContext routingContext) {

		final Actuador actuador = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		apsa.addActuador(actuador);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(actuador));
	}

	private void getSensor(RoutingContext routingContext) {
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		// Boolean cond = placaId!=null && id != null && apsa.existeSensor(id, placaId);
		Medicion medicion = apsa.getLastSensor(id, placaId);
		System.out.println(medicion);
		if (medicion != null) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(medicion));
		} else {
			// devuelve un errocete

			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(404)
					.end();
		}
	}

	private void getActuador(RoutingContext routingContext) {
//	final Integer placaId = routingContext.queryParams().contains("placaId")?Integer.parseInt(routingContext.queryParam("placaId").get(0)):null;
//	final Integer id = routingContext.queryParams().contains("id")?Integer.parseInt(routingContext.queryParam("id").get(0)):null;
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		// Boolean cond = placaId!=null && id != null && apsa.existeSensor(id, placaId);
		Actuador actuador = apsa.getLastActuador(id, placaId);
		if (actuador != null) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(actuador));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(404)
					.end();
		}

	}

	private void getAllSensores(RoutingContext routingContext) {
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		// final Integer placaId =
		// routingContext.queryParams().contains("placaId")?Integer.parseInt(routingContext.queryParam("placaId").get(0)):null;
		List<Medicion> lsAux = placaId != null && apsa.existePlaca(placaId) ? apsa.getLastSensoresList(placaId)
				: new ArrayList<Medicion>();
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lsAux));
	}

	private void getAllActuadores(RoutingContext routingContext) {
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		List<Actuador> lsAux = placaId != null && apsa.existePlaca(placaId) ? apsa.getLastActuadoresList(placaId)
				: new ArrayList<Actuador>();
		System.out.println(placaId);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lsAux));
	}

	// creamos el stop
	public void stop(Promise<Void> stopPromise) throws Exception {
		try {
			msc.close();
			stopPromise.complete();
		} catch (Exception e) {
			stopPromise.fail(e);
		}
		super.stop(stopPromise);
	}

	////////////////////////////////////////////////////////////////////////////////////
	// Funciones Auxiliares
	
	private void insertMedicion(Medicion med) {
				// Creo que es mejor no usarla 
	
			msc.getConnection(c-> {
				c.result().preparedQuery("INSERT INTO Proyecto_DAD.mediciones(medicionId, placaId, concentracion, fecha, groupId) VALUES (?,?,?,?,?)").
				execute(Tuple.of(med.getIdSensor(),med.getPlacaId(),med.getConcentracion(),med.getTimestamp(),med.getIdGroup()), r->{
				if(r.succeeded()) {
					
				}else {
					System.out.println("Error:"+r.cause().getLocalizedMessage());
				}
				});
			});
		

	}

	private void insertActuador(Actuador act) {
		msc.getConnection(c-> {
			c.result().preparedQuery("INSERT INTO Proyecto_DAD.actuadores(actuadorId, placaId, statusValue, fecha, groupId) VALUES (?,?,?,?,?)").
			execute(Tuple.of(act.getIdActuador(),act.getPlacaId(),act.getStatus(),act.getTimestamp(),act.getIdGroup()), r->{
			if(r.succeeded()) {
				
			}else {
				System.out.println("Error:"+r.cause().getLocalizedMessage());
			}
			});
		});
	}

	private void insertPlaca(Placa placa) {
		
	
		msc.getConnection(c-> {
			c.result().preparedQuery("INSERT INTO placas(placaId) VALUES (?)").
			execute(Tuple.of(placa.getId()), r->{
			if(r.succeeded()) {
				
			}else {
				System.out.println("Error:"+r.cause().getLocalizedMessage());
			}
			});
		});
	}

	private void generaRandomData() {
		try {
			final int nGroup= 5;// numero de grupos
			final int nElementos = 5;// numero de sensores y actuadores por cada grupo
			int nPlaca = 0;
			for(int g = 0; g<nGroup;g++) {
				for(int e=0; e<nElementos;e++) {
					insertPlaca(new Placa(nPlaca));
					Medicion med = Medicion.random(e, nPlaca, g);
					insertMedicion(med);
					System.out.println(med);
					nPlaca++;
					insertPlaca(new Placa(nPlaca));
					insertActuador(Actuador.random(e, nPlaca, g));
					nPlaca++;
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
