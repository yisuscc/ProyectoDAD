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
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class RestServer extends AbstractVerticle {
	private Gson gson;
	private AglutinadorPlacaSensorActuador apsa;
	private MySQLPool msc;

	public void start(Promise<Void> startFuture) {
		
		
	
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
		//Los datos sinteticos están en la bd
		// Definimos el router
		// que se encarga de coger las apis y redirigirlas
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8041, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail("El lanzamiento del servidor ha fallado" + result.cause());
			}
		});
/////////////////////////////////////////////////////////////////////
		// Asociamos las funciones a una api

		router.route("/api/*").handler(BodyHandler.create());
		// DADOS UNA Placa y un id de sensor o actuador
		// 1primera api añade una medición
		router.post("/api/sensor").handler(this::setSensor);
		// 2 devuelve la ultima medición
		router.get("/api/lastsensor/:placaId/:id").handler(this::getSensor);
		// 3 devuelve el ultimo estado de un actuador;
		router.get("/api/lastactuador/:placaId/:id").handler(this::getActuador);
		// 4 añade el estado de un actuador;
		router.post("/api/actuador").handler(this::setActuador);
		// Dada una placa id
		// 5 lo mismo pero devolviendo el ultimo estado de todos los actuadores o
		// sensores de una placa dada;
		router.get("/api/sensores/:placaId").handler(this::getAllSensores);
		router.get("/api/actuadores/:placaId").handler(this::getAllActuadores);
		
		// TODO: hacer que devuelv todo los valores del os sensores y actuadores de
		// unmismo group id
		router.get("/api/lastactuadorGroupId/:groupId").handler(this::getLastActuadorGroupId);
		router.get("/api/lastsensorGroupId/:groupId").handler(this::getLastSensorGroupId);
		// TODO: Hacer una devuelva el historico de todos los valores de un sensor o
		// actuador;
		router.get("/api/allsensor/:placaId/:id").handler(this::getAllSensor);
		router.get("/api/allactuador/:placaId/:id").handler(this::getAllActuador);
		
		// TODO:  Opcional hacer una que de la última medición a partir de una hora dada?

	}

////////////////////////////////////////////////////////////////////////////
	// definimos las llamadas del handler
	private void setSensor(RoutingContext routingContext) {
		// TODO: ADAPTAR
		final Medicion medicion = gson.fromJson(routingContext.getBodyAsString(), Medicion.class);
		apsa.addSensor(medicion);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(medicion));
	}

	private void setActuador(RoutingContext routingContext) {
		// TODO: ADAPTAR
		final Actuador actuador = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		apsa.addActuador(actuador);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(actuador));
	}

	private void getSensor(RoutingContext routingContext) {
		// TODO: No funciona
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		System.out.println("se ejecuta la petición");
		msc.getConnection(cn -> {
			if (cn.succeeded()) {
				cn.result().preparedQuery(
						"SELECT * FROM mediciones WHERE medicionId = ? AND placaId= ?")
						.execute(Tuple.of(id, placaId), res -> {
							if (res.succeeded()) {
								System.out.println("Funciona lA QUERY");
								RowSet<Row> resultSet = res.result();
								List<Medicion> result = new ArrayList<>();
								for (Row elem : resultSet) {
									result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
											elem.getLong("fecha"), elem.getDouble("concentracion"),
											elem.getInteger("groupId")));
								}
								// imprime el mensaje por consola pero no lo envia
								System.out.println(result.toString());
								// lo enviamos en el response header con un 200
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(gson.toJson(result));
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());

							}
							cn.result().close();
						});
			} else {
				System.out.println(cn.cause().toString());
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(404).end();

			}
		});
	}

	private void getActuador(RoutingContext routingContext) {
		// TODO: ADAPTAR
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		//No se cual es mejor 
		//String query = "SELECT * FROM actuadores WHERE placaId = ? AND actuadorId = ? ORDER BY fecha DESC LIMIT 1";
		String query = "SELECT * FROM actuadores WHERE placaId = ? AND actuadorId = ? ";
		msc.getConnection(con-> {
			if(con.succeeded()) {
				// si la conexion ha tenido exíto 
				//hacemos la query 
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).
				execute(Tuple.of(placaId,id),res -> {
					
			if(res.succeeded()) {
				// si la query ha tenido exito
				// cogemos el resul set
				RowSet<Row> resultSet = res.result();
				List<Actuador> result= new ArrayList<>();
				for(Row elem : resultSet) {
					// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status, Integer idGroup)
					result.add(new Actuador(elem.getInteger("actuadorId"),elem.getInteger("placaId"),elem.getLong("fecha"),elem.getBoolean("statusValue"),elem.getInteger("idGroup"))); //TODO: Meter los elementos
				
				}
				//para que aparezca en el terminal 
				System.out.println(result.toString());
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).
				end(gson.toJson(result));
			}else {
				// si la query no ha tenido exito 
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(404).
				end();
			}
			//cerramos la conexion 
			con.result().close();
				});
				
			}else {
				// si la conexion no ha tenido exito
				//imprimimos un mensaje de error
				System.out.println("Error:"+con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers 
				// un 500 o un 400? 
				//Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
				.end();
			}
		});
	}

	private void getAllSensores(RoutingContext routingContext) {
		// TODO: ADAPTAR
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		// final Integer placaId =
		// routingContext.queryParams().contains("placaId")?Integer.parseInt(routingContext.queryParam("placaId").get(0)):null;
		List<Medicion> lsAux = placaId != null && apsa.existePlaca(placaId) ? apsa.getLastSensoresList(placaId)
				: new ArrayList<Medicion>();
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lsAux));
	}

	private void getAllActuadores(RoutingContext routingContext) {
		// TODO: ADAPTAR
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		List<Actuador> lsAux = placaId != null && apsa.existePlaca(placaId) ? apsa.getLastActuadoresList(placaId)
				: new ArrayList<Actuador>();
		System.out.println(placaId);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lsAux));
	}
	
	
	private void getLastActuadorGroupId(RoutingContext routingContext) {
		//TODO
	}
	private void getLastSensorGroupId(RoutingContext routingContext) {
		//TODO
	}
	private void getAllSensor(RoutingContext routingContext) {
		//TODO
	}
	private void getAllActuador(RoutingContext routingContext) {
		//TODO 
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

		msc.getConnection(c -> {
			c.result().preparedQuery(
					"INSERT INTO Proyecto_DAD.mediciones(medicionId, placaId, concentracion, fecha, groupId) VALUES (?,?,?,?,?)")
					.execute(Tuple.of(med.getIdSensor(), med.getPlacaId(), med.getConcentracion(), med.getTimestamp(),
							med.getIdGroup()), r -> {
								if (r.succeeded()) {

								} else {
									System.out.println("Error:" + r.cause().getLocalizedMessage());
								}
							});
			c.result().close();
		});

	}

	private void insertActuador(Actuador act) {
		msc.getConnection(c -> {
			c.result().preparedQuery(
					"INSERT INTO Proyecto_DAD.actuadores(actuadorId, placaId, statusValue, fecha, groupId) VALUES (?,?,?,?,?)")
					.execute(Tuple.of(act.getIdActuador(), act.getPlacaId(), act.getStatus(), act.getTimestamp(),
							act.getIdGroup()), r -> {
								if (r.succeeded()) {

								} else {
									System.out.println("Error:" + r.cause().getLocalizedMessage());
								}
							});
			c.result().close();
		});
	}

	private void insertPlaca(Placa placa) {

		msc.getConnection(c -> {
			c.result().preparedQuery("INSERT INTO placas(placaId) VALUES (?)").execute(Tuple.of(placa.getId()), r -> {
				if (r.succeeded()) {

				} else {
					System.out.println("Error:" + r.cause().getLocalizedMessage());
				}
			});
			c.result().close();
		});
	}

	private void generaRandomData() {
		try {
			final int nGroup = 5;// numero de grupos
			final int nElementos = 5;// numero de sensores y actuadores por cada grupo
			int nPlaca = 0;
			for (int g = 0; g < nGroup; g++) {
				for (int e = 0; e < nElementos; e++) {
					insertPlaca(new Placa(nPlaca));
					Medicion med = Medicion.random(e, nPlaca, g);
					insertMedicion(med);
					insertActuador(Actuador.random(e, nPlaca, g));
					nPlaca++;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
