package es.us.lsi.dad;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class Sensores {
	private Set<Sensor> sensores;

	public Sensores(Set<Sensor> sensores) {
		super();
		this.sensores = sensores;
	}

	public Sensores() {
		super();
		this.sensores = new HashSet<Sensor>();
	}

	public Boolean addSensor(Sensor sensor) {
		return sensores.add(sensor);
	}

	public Boolean deleteSensor(Integer id) {
		Boolean res = false;
		// si alguno de los sensores tiene ese id
		for (Sensor s : sensores) {
			if (s.getId().equals(id)) {
				res = sensores.remove(s);
				break;
			}
		}
		return res;
	}

	public Boolean updateValue(Integer id, float value, long timestamp) {
		Boolean res = false;
		for (Sensor s : sensores) {
			if (s.getId().equals(id)) {
				s.setValue(value);
				s.setTimestamp(timestamp);
				res = true;
				break;
			}
		}
		return res;
	}

	public static Sensores sensoresRandom(Integer nSensores) {
		Sensores collectionDeSensores = new Sensores();
		for (int i = 0; i < nSensores; i++) {
			Sensor sensorcito = Sensor.randomSensorWithID(i);
			collectionDeSensores.addSensor(sensorcito);
		}
		return collectionDeSensores;
	}

	public Boolean contieneSensor(int id) {
		Boolean res = false;
		for (Sensor s : sensores) {
			if (s.getId().equals(id)) {
				res = true;
				break;
			}
		}
		return res;
	}
	public Sensor getSensor(Integer id) {
		Sensor res = null;
		for (Sensor s : sensores) {
			if (s.getId().equals(id)) {
				res = s;
				break;
			}
		}
		return res;
	}

	@Override
	public String toString() {
		String aux = "";
		for(Sensor e : sensores) {
			aux = aux+ e.toString()+"\r\n";
		}
		return "Sensores [sensores=" + aux + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(sensores);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensores other = (Sensores) obj;
		return Objects.equals(sensores, other.sensores);
	}
}
