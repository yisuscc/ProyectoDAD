package rest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AglutinadorPlacaSensorActuador {
	// n este caso todos los id de actuadores y sensores deben de ser únicos, indepndientemente del id de placa 
private Map<Integer, List<Placa>> mapaPlaca;
private Map<Integer, List<Sensor>> mapaSensores;
private Map<Integer, List<Actuadores>> mapaActuador;
public AglutinadorPlacaSensorActuador(Map<Integer, List<Placa>> mapaPlaca, Map<Integer, List<Sensor>> mapaSensores,
		Map<Integer, List<Actuadores>> mapaActuador) {
	super();
	this.mapaPlaca = mapaPlaca;
	this.mapaSensores = mapaSensores;
	this.mapaActuador = mapaActuador;
}
public Map<Integer, List<Placa>> getMapaPlaca() {
	return mapaPlaca;
}
public void setMapaPlaca(Map<Integer, List<Placa>> mapaPlaca) {
	this.mapaPlaca = mapaPlaca;
}
public Map<Integer, List<Sensor>> getMapaSensores() {
	return mapaSensores;
}
public void setMapaSensores(Map<Integer, List<Sensor>> mapaSensores) {
	this.mapaSensores = mapaSensores;
}
public Map<Integer, List<Actuadores>> getMapaActuador() {
	return mapaActuador;
}
public void setMapaActuador(Map<Integer, List<Actuadores>> mapaActuador) {
	this.mapaActuador = mapaActuador;
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
public static AglutinadorPlacaSensorActuador getRandomData(int i) {
	// TODO Auto-generated method stub
	return null;
}


}
