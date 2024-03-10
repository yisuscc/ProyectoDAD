package es.us.lsi.dad;

import java.util.Objects;

public class Sensor {
private Integer id;
private String name;
private String tipo;
private Long  timestamp;
private Float value;
//placaID  es util en el futuro 


public static Sensor randomSensor() {
	Integer id = (int)Math.random();
	String name = ((Double)Math.random()).toString();
	String tipo = "CO2";
	Long timestamp = System.currentTimeMillis();
	Float value  =(float)Math.random();
	return new Sensor(id, name, tipo, timestamp, value);
}public static Sensor randomSensorWithID(Integer id) {
	
	String name =((Double)Math.random()).toString();
	String tipo = "CO2";
	Long timestamp = System.currentTimeMillis();
	Float value  =(float)Math.random();
	return new Sensor(id, name, tipo, timestamp, value);
}
public Sensor(Integer id, String name, String tipo, long timestamp, float value) {
	// añadimos los checkers
	super();
	this.id = id;
	this.name = name;
	this.tipo = tipo;
	this.timestamp = timestamp;
	this.value = value;
}
public Sensor() {
	super();
	
}
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getTipo() {
	return tipo;
}
public void setTipo(String tipo) {
	this.tipo = tipo;
}
public Long getTimestamp() {
	return timestamp;
}
public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
}
public Float getValue() {
	return value;
}
public void setValue(float value) {
	this.value = value;
}
@Override
public int hashCode() {
	return Objects.hash(id);
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Sensor other = (Sensor) obj;
	return Objects.equals(id, other.id);
}
@Override
public String toString() {
	return "Sensor [id=" + id + ", name=" + name + ", tipo=" + tipo + ", timestamp=" + timestamp + ", value=" + value
			+ "]";
}



}
