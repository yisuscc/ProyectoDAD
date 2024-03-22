package rest;

import java.security.interfaces.DSAKeyPairGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AglutinadorPlacaSensorActuador {
	// un sensor/actuador es unico si su combinacion de sensor/actuador y placa id lo es 
private Map<Integer, List<Placa>> mapaPlaca;
private Map<Par, List<Sensor>> mapaSensores;
private Map<Par, List<Actuador>> mapaActuador;


public AglutinadorPlacaSensorActuador(Map<Integer, List<Placa>> mapaPlaca, Map<Par, List<Sensor>> mapaSensores,
		Map<Par, List<Actuador>> mapaActuador) {
	super();
	this.mapaPlaca = mapaPlaca;
	this.mapaSensores = mapaSensores;
	this.mapaActuador = mapaActuador;
}
public AglutinadorPlacaSensorActuador() {
	super();
	this.mapaPlaca = new HashMap<Integer, List<Placa>>();
	this.mapaSensores = new HashMap<Par, List<Sensor>>();
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
public Map<Par, List<Sensor>> getMapaSensores() {
	return mapaSensores;
}
public void setMapaSensores(Map<Par, List<Sensor>> mapaSensores) {
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
	return mapaPlaca.put(placa.getId(),List.of(placa));
}
// añade dato sensor 
// boolean o list ?
public List<Sensor> addSensor(Sensor sensor) {
	//se puede poner mas bonito 
	
	// si  no existe la placa 
	if(!existePlaca(sensor.getPlacaId())) {
		//la creamos 
	addPlaca(new Placa(sensor.getPlacaId()));
	}
	// si existe el par sensor placa
	Par clave = Par.of(sensor.getId(), sensor.getPlacaId());
	List<Sensor>valor = new ArrayList<Sensor>();
	if(mapaSensores.containsKey(clave)) {
		valor = mapaSensores.get(clave);
	}
	valor.add(sensor);
	mapaSensores.put(clave, valor);
	return  valor;
}
// añade dato actuador 
public List<Actuador> addActuador(Actuador actuador){
	//se puede poner mas bonito 
	
	// si  no existe la placa 
	if(!existePlaca(actuador.getPlacaId())) {
		//la creamos 
	addPlaca(new Placa(actuador.getPlacaId()));
	}
	// si existe el par sensor placa
	Par clave = Par.of(actuador.getId(), actuador.getPlacaId());
	List<Actuador>valor = new ArrayList<Actuador>();
	if(mapaActuador.containsKey(clave)) {
		valor = mapaActuador.get(clave);
	}
	valor.add(actuador);
	mapaActuador.put(clave, valor);
	return  valor;
}
//considero que las lecturas  se pueden hacer directamente  en la clase rest
//server  con  los getter de los  mapas y un procesamiento despues 
public static AglutinadorPlacaSensorActuador getRandomData(int i) {
	AglutinadorPlacaSensorActuador res = new AglutinadorPlacaSensorActuador();
	// se generan i placas 
	// cada placa tendñra asociado 5 sensores e 5 actuadores 
	for(int j= 0; j<i;j++) {
		//creamos laplaca
		if(!res.existePlaca(j)) {
			res.addPlaca(new Placa(j));
		}
		for(int k = 0; k<i;k++) {
			res.addSensor(Sensor.random(k, j));
		}
		for(int l = 0; l<i; l++) {
			res.addActuador(Actuador.random(l,j));
		}
	}
	return res;
}
public void clear() {
	this.mapaActuador.clear();
	this.mapaPlaca.clear();
	this.mapaSensores.clear();
}


}
