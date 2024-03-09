package es.us.lsi.dad;

import java.util.Objects;

public class Sensor {
private Integer id;
private String name;
private String tipo;
private Long  timestamp;
@Override
public String toString() {
	return "Sensor [id=" + id + ", name=" + name + ", tipo=" + tipo + ", timestamp=" + timestamp + ", value=" + value
			+ "]";
}
@Override
public int hashCode() {
	return Objects.hash(id, name, timestamp, tipo, value);
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
	return Objects.equals(id, other.id) && Objects.equals(name, other.name)
			&& Objects.equals(timestamp, other.timestamp) && Objects.equals(tipo, other.tipo) && value == other.value;
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
public void setTimestamp(Long timestamp) {
	this.timestamp = timestamp;
}
public long getValue() {
	return value;
}
public void setValue(long value) {
	this.value = value;
}
private long value;
// private Integer placaId;
public Sensor(Integer id, String name, String tipo, Long timestamp, long value) {
	super();
	this.id = id;
	this.name = name;
	this.tipo = tipo;
	this.timestamp = timestamp;
	this.value = value;
}

}
