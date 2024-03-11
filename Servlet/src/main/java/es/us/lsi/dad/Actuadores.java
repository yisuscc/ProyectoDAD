package es.us.lsi.dad;

import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Actuadores {
// es distinto a la clase de sensores
	// esta vez voy a usar un map 
	private SortedMap<Integer, Actuador> actuadores;
	// creo que el sorted map puede teren mejorias respecto
	//a un map normal

public Actuadores(SortedMap<Integer, Actuador> actuadores) {
	super();
	this.actuadores = actuadores;
}
public Actuadores() {
	super();
	this.actuadores = new TreeMap<Integer, Actuador>();
}
public SortedMap<Integer, Actuador> getActuadores() {
	return actuadores;
}
public void setActuadores(SortedMap<Integer, Actuador> actuadores) {
	this.actuadores = actuadores;
}
@Override
public String toString() {
	return "Actuadores [actuadores=" + actuadores + "]";
}
public Boolean deleteActuador(Integer id) {
	Boolean res = false;
	if(actuadores.containsKey(id)) {
		actuadores.remove(id);
		res = true;
	}
	return res; 
}
public Boolean updateActuador(Actuador actdr) {
	Boolean res = false ;
	// contene el actuador
	if(actuadores.containsKey(actdr.getId())) {
		actuadores.replace(actdr.getId(), actdr);
		res = true;
	}
	//no contiene el actuador y lo añade 
	else {
		res = addActuador(actdr);
	}
	return res;
}
public Boolean addActuador(Actuador actdr) {
	Boolean res = false ;
	if(!actuadores.containsKey(actdr.getId())) {
		actuadores.put(actdr.getId(), actdr);
		res = true;
	}
	
	return res;
}
public Actuador getActuador(Integer id) {
	Actuador res = null;
	if (actuadores.containsKey(id)) {
		res= actuadores.get(id);
	}
	return res;
}
/*
 * public static Sensores sensoresRandom(Integer nSensores) {
		Sensores collectionDeSensores = new Sensores();
		for (int i = 0; i < nSensores; i++) {
			Sensor sensorcito = Sensor.createRandomSensorWithId(i);
			collectionDeSensores.addSensor(sensorcito);
		}
		return collectionDeSensores;
	}
 */
public  Boolean contieneActuador(Integer id) {
	return actuadores.containsKey(id);
}
public static Actuadores actuadoresRandom(Integer nActuadores) {
	Actuadores collectionDeActuadores = new Actuadores();
	for(int i= 0; i<nActuadores; i++) {
		collectionDeActuadores.addActuador(Actuador.getActuadorRandomWithId(i));
	}
	return collectionDeActuadores;
}
	

	

}
