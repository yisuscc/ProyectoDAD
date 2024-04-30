package rest;

import java.security.interfaces.DSAKeyPairGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AglutinadorPlacaSensorActuador {
	// un sensor/actuador es unico si su combinacion de sensor/actuador y placa id
	// lo es
	private Map<Integer, List<Placa>> mapaPlaca;
	private Map<Par, List<Medicion>> mapaSensores;
	private Map<Par, List<Actuador>> mapaActuador;

	public AglutinadorPlacaSensorActuador(Map<Integer, List<Placa>> mapaPlaca, Map<Par, List<Medicion>> mapaSensores,
			Map<Par, List<Actuador>> mapaActuador) {
		super();
		this.mapaPlaca = mapaPlaca;
		this.mapaSensores = mapaSensores;
		this.mapaActuador = mapaActuador;
	}

	public AglutinadorPlacaSensorActuador() {
		super();
		this.mapaPlaca = new HashMap<Integer, List<Placa>>();
		this.mapaSensores = new HashMap<Par, List<Medicion>>();
		this.mapaActuador = new HashMap<Par, List<Actuador>>();
	}

	@Override
	public String toString() {
		return "AglutinadorPlacaSensorActuador [mapaPlaca=" + mapaPlaca + ", mapaSensores=" + mapaSensores
				+ ", mapaActuador=" + mapaActuador + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(mapaActuador, mapaPlaca, mapaSensores);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AglutinadorPlacaSensorActuador other = (AglutinadorPlacaSensorActuador) obj;
		return Objects.equals(mapaActuador, other.mapaActuador) && Objects.equals(mapaPlaca, other.mapaPlaca)
				&& Objects.equals(mapaSensores, other.mapaSensores);
	}

	public Map<Integer, List<Placa>> getMapaPlaca() {
		return mapaPlaca;
	}

	public void setMapaPlaca(Map<Integer, List<Placa>> mapaPlaca) {
		this.mapaPlaca = mapaPlaca;
	}

	public Map<Par, List<Medicion>> getMapaSensores() {
		return mapaSensores;
	}

	public void setMapaSensores(Map<Par, List<Medicion>> mapaSensores) {
		this.mapaSensores = mapaSensores;
	}

	public Map<Par, List<Actuador>> getMapaActuador() {
		return mapaActuador;
	}

	public void setMapaActuador(Map<Par, List<Actuador>> mapaActuador) {
		this.mapaActuador = mapaActuador;
	}

//existePlaca
	public Boolean existePlaca(Integer id) {
		return mapaPlaca.containsKey(id);
	}

// addPlaca
	public List<Placa> addPlaca(Placa placa) {
		return mapaPlaca.put(placa.getId(), List.of(placa));
	}

// añade dato sensor 
// boolean o list ?
	public List<Medicion> addSensor(Medicion medicion) {
		// se puede poner mas bonito

		// si no existe la placa
		if (!existePlaca(medicion.getPlacaId())) {
			// la creamos
			addPlaca(new Placa(medicion.getPlacaId()));
		}
		// si existe el par sensor placa
		Par clave = Par.of(medicion.getId(), medicion.getPlacaId());
		List<Medicion> valor = new ArrayList<Medicion>();
		if (mapaSensores.containsKey(clave)) {
			valor = mapaSensores.get(clave);
		}
		valor.add(medicion);
		mapaSensores.put(clave, valor);
		return valor;
	}

// añade dato actuador 
	public List<Actuador> addActuador(Actuador actuador) {
		// se puede poner mas bonito

		// si no existe la placa
		if (!existePlaca(actuador.getPlacaId())) {
			// la creamos
			addPlaca(new Placa(actuador.getPlacaId()));
		}
		// si existe el par sensor placa
		Par clave = Par.of(actuador.getId(), actuador.getPlacaId());
		List<Actuador> valor = new ArrayList<Actuador>();
		if (mapaActuador.containsKey(clave)) {
			valor = mapaActuador.get(clave);
		}
		valor.add(actuador);
		mapaActuador.put(clave, valor);
		return valor;
	}

	public Map<Integer, List<Medicion>> getSensoresPlaca(Integer placaID) {
		// Devuelve todos las concetracionesde todos sensores asociados a una placa
		return mapaSensores.entrySet().stream().filter(e -> e.getKey().placaId().equals(placaID))
				.collect(Collectors.toMap(e -> e.getKey().id(), e -> e.getValue()));
	}
public Boolean existeSensor(Integer id,Integer placaId) {
	return mapaSensores.get(Par.of(id, placaId)) != null;
}
public Boolean existeActuador(Integer id,Integer placaId) {
	return mapaActuador.containsKey(Par.of(id, placaId));
}
//los añado aqúi para simplificar el codigo 
	public Map<Integer, Medicion> getLastSensoresPlaca(Integer placaID) {
		return getSensoresPlaca(placaID).entrySet().stream().map(e -> {
			return e.getValue().get(e.getValue().size() - 1);
		}).collect(Collectors.toMap(s -> s.getId(), s -> s));
	}
public List<Medicion> getLastSensoresList(Integer placaId){
	return getLastSensoresPlaca(placaId).entrySet().stream().map(e-> e.getValue()).toList();
}
	public Medicion getLastSensor(Integer id, Integer placaId) {
		List<Medicion> lsAux = mapaSensores.get(Par.of(id, placaId));
		return lsAux.get(lsAux.size() - 1);
	}

	public Map<Integer, List<Actuador>> getActuadoresPlaca(Integer placaID) {
		// Devuelve todos las concetracionesde todos sensores asociados a una placa
		return mapaActuador.entrySet().stream().filter(e -> e.getKey().placaId().equals(placaID))
				.collect(Collectors.toMap(e -> e.getKey().id(), e -> e.getValue()));
	}

	public Map<Integer, Actuador> getLastActuadoresPlaca(Integer placaID) {
		return getActuadoresPlaca(placaID).entrySet().stream().map(e -> {
			return e.getValue().get(e.getValue().size() - 1);
		}).collect(Collectors.toMap(s -> s.getId(), s -> s));
	}
	public List<Actuador> getLastActuadoresList(Integer placaId){
		return getLastActuadoresPlaca(placaId).entrySet().stream().map(e-> e.getValue()).toList();
	}
	public Actuador getLastActuador(Integer id, Integer placaId) {
		List<Actuador> lsAux = mapaActuador.get(Par.of(id, placaId));
		return lsAux.get(lsAux.size() - 1);
	}

	public static AglutinadorPlacaSensorActuador getRandomData(int i) {
		AglutinadorPlacaSensorActuador res = new AglutinadorPlacaSensorActuador();
		// se generan i placas
		// cada placa tendñra asociado 5 sensores e 5 actuadores
		// y 2 mediciones o estados
		try {
			for (int j = 0; j < i; j++) {
				// creamos laplaca
				if (!res.existePlaca(j)) {
					res.addPlaca(new Placa(j));
				}
				for (int k = 0; k < i; k++) {
					res.addSensor(Medicion.random(k, j));
					TimeUnit.MILLISECONDS.sleep(2);
					res.addSensor(Medicion.random(k, j));
				}
				for (int l = 0; l < i; l++) {
					res.addActuador(Actuador.random(l, j));
					TimeUnit.MILLISECONDS.sleep(2);
					res.addActuador(Actuador.random(l, j));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public void clear() {
		this.mapaActuador.clear();
		this.mapaPlaca.clear();
		this.mapaSensores.clear();
	}

}
