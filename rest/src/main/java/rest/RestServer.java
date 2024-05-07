package rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class RestServer extends AbstractVerticle {
	// parmetros modificables:
	final String ipMqttServer = "localhost";
	final Integer puertoAPIRest = 8043;
	final Double umbral = 37.0;
	// variabless privadas e inmodificables
	private Gson gson;
	private MySQLPool msc;
	private MqttClient mqttClient;

	public void start(Promise<Void> startFuture) {

		// 1)Configuramos los datos del gson;
		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // ISO-8601 FTW

		// 2) Nos conectamos a la base de datos.
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("proyecto_dad").setUser("root").setPassword("root");
		// mediante la MYSQLConnectOptions.
		PoolOptions poolOptions = new PoolOptions().setMaxSize(37); // por poner un numero
		msc = MySQLPool.pool(vertx, connectOptions, poolOptions);

		/*
		 * Nota:el usuario y contrasena de root es root Alternativamente PDAD es PDAD.
		 */
		// 3) inicializamos el mqtt
		mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, ipMqttServer, s -> sendMessage("1234", "Hola"));

		// 4)Definimos el router
		// que se encarga de coger las apis y redirigirlas
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(puertoAPIRest, result -> {
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

		// Hacer que devuelva los ultimos valores de todos los sensores y actuadores de
		// un mismo group id
		router.get("/api/lastactuadorGroupId/:groupId").handler(this::getLastActuadorGroupId);
		router.get("/api/lastsensorGroupId/:groupId").handler(this::getLastSensorGroupId);
		// Devuelve todos los valores de todos los valores y sensores de un mismo group
		// id
		router.get("/api/allactuadorGroupId/:groupId").handler(this::getAllActuadorGroupID);
		router.get("/api/allsensorGroupId/:groupId").handler(this::getAllSensorGroupID);
		// Hacer una devuelva el historico de todos los valores de un sensor o
		// actuador;
		router.get("/api/allsensor/:placaId/:id").handler(this::getAllSensor);
		router.get("/api/allactuador/:placaId/:id").handler(this::getAllActuador);

		// TODO: Opcional hacer una que de la última medición a partir de una hora dada?

	}

////////////////////////////////////////////////////////////////////////////
	// definimos las llamadas del handler
	// Idoneamente, para los gets creariamos una funcion auxiliar que se encarga del
	// al conexion a la base de datos.
	private void setSensor(RoutingContext routingContext) {
// TODO: añadir la conectividad mqtt para activar los actuadores de un group id 
		// cuando se sobrepasa un umbral
		final Medicion medicion = gson.fromJson(routingContext.getBodyAsString(), Medicion.class);
		String query = "INSERT INTO Proyecto_DAD.mediciones(medicionId, placaId, concentracion, fecha, groupId) VALUES (?,?,?,?,?)";
		String topic = medicion.getIdGroup().toString();
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(Tuple.of(medicion.getIdSensor(), medicion.getPlacaId(),
						medicion.getConcentracion(), medicion.getTimestamp(), medicion.getIdGroup()), res -> {
							if (res.succeeded()) {
								// si la query ha tenido exito
								// Devolvemos el sensor insertado
								System.out.println("Insertado: " + medicion.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(gson.toJson(medicion));
								// TODO Lógica mqttt;
								// 1) obtenemos la ultima medición de cada sensor:
								//List<Medicion> ultimasMediciones = obtainLastMeasurementsFromGroupId(
										//medicion.getIdGroup());// TODO implementar
								/*
								 * Esto dependerá de la logica que implementemos: 2 opciones: 1. que cuando
								 * cualquiera de los sensores supere el umbral se active 2.(descartada) que
								 * cuando la media de los sensores supere el umbral se activen
								 */
								// 2) evaluamos la condicion
								checkCondition(medicion.getIdGroup());
								System.out.println("condicion evaluada");
							

							} else {
								// si la query no ha tenido exito
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(404).end();
							}
							// cerramos la conexion
							con.result().close();
						});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});
	}

	private void setActuador(RoutingContext routingContext) {
		// TODO añadir la logíca de mqtt
		final Actuador actuador = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		String query = "INSERT INTO Proyecto_DAD.actuadores(actuadorId, placaId, statusValue, fecha, groupId) VALUES (?,?,?,?,?)";
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(Tuple.of(actuador.getIdActuador(), actuador.getPlacaId(),
						actuador.getStatus(), actuador.getTimestamp(), actuador.getIdGroup()), res -> {
							if (res.succeeded()) {
								// si la query ha tenido exito
								// Devolvemos el sensor insertado
								System.out.println("Insertado: " + actuador.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(gson.toJson(actuador));
							} else {
								// si la query no ha tenido exito
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(404).end();
							}
							// cerramos la conexion
							con.result().close();
						});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});
	}

	private void getAllSensor(RoutingContext routingContext) {
		// Devuelve todas las mediciones de un sensor
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		String query = "SELECT * FROM mediciones WHERE placaId = ? AND medicionId = ? ORDER BY fecha DESC";
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(Tuple.of(placaId, id), res -> {

					if (res.succeeded()) {
						// si la query ha tenido exito
						// cogemos el resul set
						RowSet<Row> resultSet = res.result();
						List<Medicion> result = new ArrayList<>();
						for (Row elem : resultSet) {
							// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status,
							// Integer idGroup)
							result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getDouble("concentracion"),
									elem.getInteger("groupId")));

						}
						// para que aparezca en el terminal
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});
	}

	private void getAllActuador(RoutingContext routingContext) {
		// Devuelve todos los estados de un actuador.
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));

		String query = "SELECT * FROM actuadores WHERE placaId = ? AND actuadorId = ? ORDER BY fecha DESC ";
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(Tuple.of(placaId, id), res -> {

					if (res.succeeded()) {
						// si la query ha tenido exito
						// cogemos el resul set
						RowSet<Row> resultSet = res.result();
						List<Actuador> result = new ArrayList<>();
						for (Row elem : resultSet) {
							// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status,
							// Integer idGroup)
							result.add(new Actuador(elem.getInteger("actuadorId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getBoolean("statusValue"), elem.getInteger("groupId")));
						}
						// para que aparezca en el terminal
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});
	}

	private void getAllSensores(RoutingContext routingContext) {

		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		String query = "SELECT * FROM mediciones WHERE placaId = ?";
		Tuple tupla = Tuple.of(placaId);
		retrieveSensorDB(query, tupla, routingContext);
	}

	private void getAllActuadores(RoutingContext routingContext) {

		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		String query = "SELECT * FROM actuadores WHERE placaId = ?";
		Tuple tupla = Tuple.of(placaId);
		retrieveActuadorDB(query, tupla, routingContext);

	}

	private void getLastActuadorGroupId(RoutingContext routingContext) {
		// devuelve los ultimos valores de tods los actyuadores de unmismo group id

		final Integer groupId = Integer.parseInt(routingContext.request().getParam("groupId"));
		String query = "SELECT * FROM actuadores WHERE groupId = ?";
		Tuple tupla = Tuple.of(groupId);
		msc.getConnection(con -> {
			if (con.succeeded()) {

				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(tupla, res -> {

					if (res.succeeded()) {

						RowSet<Row> resultSet = res.result();
						List<Actuador> result = new ArrayList<>();
						for (Row elem : resultSet) {

							result.add(new Actuador(elem.getInteger("actuadorId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getBoolean("statusValue"), elem.getInteger("groupId")));

						}
						result = ultimoActuadoresGroupID(result);
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});

	}

	private void getLastSensorGroupId(RoutingContext routingContext) {
		// devuelve los ultimos valores de todos los sensores de unmismo group id

		final Integer groupId = Integer.parseInt(routingContext.request().getParam("groupId"));
		String query = "SELECT * FROM mediciones WHERE groupId = ?";
		Tuple tupla = Tuple.of(groupId);
		msc.getConnection(con -> {
			if (con.succeeded()) {

				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(tupla, res -> {

					if (res.succeeded()) {

						RowSet<Row> resultSet = res.result();
						List<Medicion> result = new ArrayList<>();
						for (Row elem : resultSet) {

							result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getDouble("concentracion"),
									elem.getInteger("groupId")));

						}
						result = ultimoSensoresGroupID(result);
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});

	}

	private void getAllSensorGroupID(RoutingContext routingContext) {
		final Integer groupId = Integer.parseInt(routingContext.request().getParam("groupId"));
		String query = "SELECT * FROM mediciones WHERE groupId = ?";
		Tuple tuple = Tuple.of(groupId);
		retrieveSensorDB(query, tuple, routingContext);
	}

	private void getAllActuadorGroupID(RoutingContext routingContext) {
		final Integer groupId = Integer.parseInt(routingContext.request().getParam("groupId"));
		String query = "SELECT * FROM actuadores WHERE groupId = ?";
		Tuple tuple = Tuple.of(groupId);
		retrieveActuadorDB(query, tuple, routingContext);
	}

	private void getSensor(RoutingContext routingContext) {
		// Devuelve la última medición de un sensor
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		// No se cual es mejor
		String query = "SELECT * FROM mediciones WHERE placaId = ? AND medicionId = ? ORDER BY fecha DESC LIMIT 1";
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(Tuple.of(placaId, id), res -> {

					if (res.succeeded()) {
						// si la query ha tenido exito
						// cogemos el resul set
						RowSet<Row> resultSet = res.result();
						List<Medicion> result = new ArrayList<>();
						for (Row elem : resultSet) {
							// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status,
							// Integer idGroup)
							result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getDouble("concentracion"),
									elem.getInteger("groupId")));

						}
						// para que aparezca en el terminal
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});

	}

	private void getActuador(RoutingContext routingContext) {
//Devuelve  el ultimo valor de un cactuador especifico
		final Integer placaId = Integer.parseInt(routingContext.request().getParam("placaId"));
		final Integer id = Integer.parseInt(routingContext.request().getParam("id"));
		// No se cual es mejor
		String query = "SELECT * FROM actuadores WHERE placaId = ? AND actuadorId = ? ORDER BY fecha DESC LIMIT 1";
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(Tuple.of(placaId, id), res -> {

					if (res.succeeded()) {
						// si la query ha tenido exito
						// cogemos el resul set
						RowSet<Row> resultSet = res.result();
						List<Actuador> result = new ArrayList<>();
						for (Row elem : resultSet) {
							// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status,
							// Integer idGroup)
							result.add(new Actuador(elem.getInteger("actuadorId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getBoolean("statusValue"), elem.getInteger("groupId")));

						}
						// para que aparezca en el terminal
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});

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
	////////////////////////////////////////////////////////////////////////////////////
	private void sendMessage(String topic, String mensaje) {

		mqttClient.connect(1883, "localhost", s -> {
			mqttClient.publish(topic, Buffer.buffer(mensaje), MqttQoS.AT_LEAST_ONCE, false, false);
			});

	}
	
	

	private List<Medicion> obtainLastMeasurementsFromGroupId(Integer idGroup) {
		// TODO: COmprobar funciionamiento parece que no funciona
		List<Medicion> resultados = new ArrayList<Medicion>();
		String query = "SELECT * FROM mediciones WHERE groupId = ?";
		Tuple tupla = Tuple.of(idGroup);

		msc.getConnection(con -> {
			if (con.succeeded()) {

				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(tupla, res -> {

					if (res.succeeded()) {

						RowSet<Row> resultSet = res.result();
						List<Medicion> result = new ArrayList<>();
						for (Row elem : resultSet) {

							result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getDouble("concentracion"),
									elem.getInteger("groupId")));

						}
						result = ultimoSensoresGroupID(result);
						resultados.addAll(result);

					} else {
						// si la query no ha tenido exito
						System.out.println("Error en la query");

					}
					// cerramos la conexion
					con.result().close();

				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error

			}
		});

		return resultados;
	}

	private static List<Medicion> ultimoSensoresGroupID(List<Medicion> sensores) {
		Collection<Medicion> coleccion = sensores.stream()
				.collect(
						Collectors.groupingBy(m -> Par.of(m.getIdSensor(), m.getPlacaId()), HashMap::new,
								Collectors.collectingAndThen(
										Collectors.maxBy(Comparator.comparing(Medicion::getTimestamp)), Optional::get)))
				.values();
		return new ArrayList<Medicion>(coleccion);
	}

	private static List<Actuador> ultimoActuadoresGroupID(List<Actuador> actuadores) {
		Collection<Actuador> coleccion = actuadores.stream()
				.collect(
						Collectors.groupingBy(a -> Par.of(a.getIdActuador(), a.getPlacaId()), HashMap::new,
								Collectors.collectingAndThen(
										Collectors.maxBy(Comparator.comparing(Actuador::getTimestamp)), Optional::get)))
				.values();
		return new ArrayList<Actuador>(coleccion);
	}

	private void retrieveSensorDB(String query, Tuple tuple, RoutingContext routingContext) {
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(tuple, res -> {

					if (res.succeeded()) {
						// si la query ha tenido exito
						// cogemos el resul set
						RowSet<Row> resultSet = res.result();
						List<Medicion> result = new ArrayList<>();
						for (Row elem : resultSet) {
							// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status,
							// Integer idGroup)
							result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getDouble("concentracion"),
									elem.getInteger("groupId")));

						}
						// para que aparezca en el terminal
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});
	}

	private void retrieveActuadorDB(String query, Tuple tuple, RoutingContext routingContext) {
		msc.getConnection(con -> {
			if (con.succeeded()) {
				// si la conexion ha tenido exíto
				// hacemos la query
				System.out.println("Conexion exitosa");
				con.result().preparedQuery(query).execute(tuple, res -> {

					if (res.succeeded()) {
						// si la query ha tenido exito
						// cogemos el resul set
						RowSet<Row> resultSet = res.result();
						List<Actuador> result = new ArrayList<>();
						for (Row elem : resultSet) {
							// Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status,
							// Integer idGroup)
							result.add(new Actuador(elem.getInteger("actuadorId"), elem.getInteger("placaId"),
									elem.getLong("fecha"), elem.getBoolean("statusValue"), elem.getInteger("groupId")));

						}
						// para que aparezca en el terminal
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(gson.toJson(result));
					} else {
						// si la query no ha tenido exito
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(404).end();
					}
					// cerramos la conexion
					con.result().close();
				});

			} else {
				// si la conexion no ha tenido exito
				// imprimimos un mensaje de error
				System.out.println("Error:" + con.cause().toString());
				// adicionalmente devolmvemos un mensaje de error en elos headers
				// un 500 o un 400?
				// Creo que un 500 ya que es problema entre vertx y la bbdd
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(500).end();
			}
		});
	}

private void checkCondition(Integer groupID) {
	//final Integer groupId = Integer.parseInt(routingContext.request().getParam("groupId"));
	String query = "SELECT * FROM mediciones WHERE groupId = ?";
	Tuple tupla = Tuple.of(groupID);
	msc.getConnection(con -> {
		if (con.succeeded()) {

			System.out.println("Conexion exitosa");
			con.result().preparedQuery(query).execute(tupla, res -> {

				if (res.succeeded()) {

					RowSet<Row> resultSet = res.result();
					List<Medicion> result = new ArrayList<>();
					for (Row elem : resultSet) {

						result.add(new Medicion(elem.getInteger("medicionId"), elem.getInteger("placaId"),
								elem.getLong("fecha"), elem.getDouble("concentracion"),
								elem.getInteger("groupId")));

					}
					result = ultimoSensoresGroupID(result);
					System.out.println(result.toString());
					Boolean cond = false;
					for (Medicion med : result) {
						if(med.getConcentracion()>= umbral) {
							cond = true;
							break;
					}
					}
					if(cond) {
						sendMessage(groupID.toString(), "1");// 
					}else {
						sendMessage(groupID.toString(), "0");
					}
				} else {
					// si la query no ha tenido exito

				}
				// cerramos la conexion
				con.result().close();
			});

		} else {
			// si la conexion no ha tenido exito
			System.out.println("Error:" + con.cause().toString());

		}
	});

}
	
}

