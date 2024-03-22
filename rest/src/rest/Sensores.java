package rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Sensores {
	//MAP(IDsensor,Lista De valores del sensor);
	//POsible mejora 
// MAP((IDPLACA,IdSensor), Lista de valores del sensor)
private  Map<Integer, List<Sensor>> sensores; //guarda 

public Sensores(Map<Integer, List<Sensor>> sensores) {
	super();
	this.sensores = sensores;
}

public Map<Integer, List<Sensor>> getSensores() {
	return sensores;
}

public void setSensores(Map<Integer, List<Sensor>> sensores) {
	this.sensores = sensores;
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

@Override
public String toString() {
	return "Sensores [sensores=" + sensores + "]";
}





}
