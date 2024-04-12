package rest;

import java.util.Objects;

public class Medicion {

	private Integer idSensor;
	private Integer placaId; //no estoy seguro de que sirva 
	private Long timestamp;
	private Double concentracion;
	private Integer idGroup;


	@Override
	public int hashCode() {
		return Objects.hash(concentracion, idGroup, idSensor, placaId, timestamp);
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
		return Objects.equals(concentracion, other.concentracion) && Objects.equals(idGroup, other.idGroup)
				&& Objects.equals(idSensor, other.idSensor) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(timestamp, other.timestamp);
	}


	@Override
	public String toString() {
		return "Medicion [idSensor=" + idSensor + ", placaId=" + placaId + ", timestamp=" + timestamp
				+ ", concentracion=" + concentracion + ", idGroup=" + idGroup + "]";
	}


	public Medicion(Integer idSensor, Integer placaId, Long timestamp, Double concentracion, Integer idGroup) {
		super();
		this.idSensor = idSensor;
		this.placaId = placaId;
		this.timestamp = timestamp;
		this.concentracion = concentracion;
		this.idGroup = idGroup;
	}


	public Integer getIdSensor() {
		return idSensor;
	}


	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
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


	public Integer getIdGroup() {
		return idGroup;
	}


	public void setIdGroup(Integer idGroup) {
		this.idGroup = idGroup;
	}


	public static Medicion random(Integer id , Integer placaId,Integer groupId) {
		Long timestamp = System.currentTimeMillis();
		Double value = Math.random()*1000;
		return new Medicion(id, placaId, timestamp, value,groupId); 
	}
}
