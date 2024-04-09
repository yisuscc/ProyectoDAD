package rest;

import java.util.Objects;

public class Medicion {
	//almacena los datos 
	// cada instancia almacena los datos de una medición de un sensor, en un momento dado 
	//mas que sensor debería ser actuador
	private Integer id;
	private Integer placaId; //no estoy seguro de que sirva 
	private Long timestamp;
	private Double concentracion;
	public Medicion(Integer id, Integer placaId, Long timestamp, Double concentracion) {
		super();
		this.id = id;
		this.placaId = placaId;
		this.timestamp = timestamp;
		this.concentracion = concentracion;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPlacaId() {
		return placaId;
	}
	public void setPlacaId(Integer placaId) {
		this.placaId = placaId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Double getConcentracion() {
		return concentracion;
	}
	public void setConcentracion(Double concentracion) {
		this.concentracion = concentracion;
	}
	@Override
	public String toString() {
		return "Sensor [id=" + id + ", placaId=" + placaId + ", timestamp=" + timestamp + ", concentracion="
				+ concentracion + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(concentracion, id, placaId, timestamp);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Medicion other = (Medicion) obj;
		return Objects.equals(concentracion, other.concentracion) && Objects.equals(id, other.id)
				&& Objects.equals(placaId, other.placaId) && Objects.equals(timestamp, other.timestamp);
	}

	public static Medicion random(Integer id , Integer placaId) {
		Long timestamp = System.currentTimeMillis();
		Double value = Math.random()*1000;
		return new Medicion(id, placaId, timestamp, value); 
	}
}
