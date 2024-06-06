package rest;

import java.util.Objects;

public class Actuador { // nuestro relesito 
	private Integer idActuador;
	private Integer placaId;
	private Long timestamp;
	private Boolean status; // si está abierto o cerrado 
	private Integer  idGroup;
	

public Actuador(Integer idActuador, Integer placaId, Long timestamp, Boolean status, Integer idGroup) {
		super();
		this.idActuador = idActuador;
		this.placaId = placaId;
		this.timestamp = timestamp;
		this.status = status;
		this.idGroup = idGroup;
	}


@Override
public String toString() {
	return "Actuador [idActuador=" + idActuador + ", placaId=" + placaId + ", timestamp=" + timestamp + ", status="
			+ status + ", idGroup=" + idGroup + "]";
}


@Override
public int hashCode() {
	return Objects.hash(idActuador, idGroup, placaId, status, timestamp);
}


@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Actuador other = (Actuador) obj;
	return Objects.equals(idActuador, other.idActuador) && Objects.equals(idGroup, other.idGroup)
			&& Objects.equals(placaId, other.placaId) && Objects.equals(status, other.status)
			&& Objects.equals(timestamp, other.timestamp);
}


public Integer getIdActuador() {
	return idActuador;
}


public void setIdActuador(Integer idActuador) {
	this.idActuador = idActuador;
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


public Boolean getStatus() {
	return status;
}


public void setStatus(Boolean status) {
	this.status = status;
}


public Integer getIdGroup() {
	return idGroup;
}


public void setIdGroup(Integer idGroup) {
	this.idGroup = idGroup;
}


public static Actuador random(Integer id, Integer placaId,Integer groupId) {

	Long timestamp = System.currentTimeMillis();
	Boolean value = Math.random()<0.5;
	return new Actuador(id, placaId, timestamp, value,groupId);
}

}
