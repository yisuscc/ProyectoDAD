package rest;

import java.util.Objects;

public class Actuador { // nuestro relesito 
	private Integer id;
	private Integer placaId;
	private Long timestamp;
	private Boolean status; // si está abierto o cerrado 
	//quizas una para estado 
	public Actuador(Integer id, Integer placaId, Long timestamp, Boolean value) {
		super();
		this.id = id;
		this.placaId = placaId;
		this.timestamp = timestamp;
		this.status = value;
	}
	@Override
	public String toString() {
		return "Actuador [id=" + id + ", placaId=" + placaId + ", timestamp=" + timestamp + ", value=" + status + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, placaId, timestamp, status);
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
		return Objects.equals(id, other.id) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(timestamp, other.timestamp) && Objects.equals(status, other.status);
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
	public Boolean getValue() {
		return status;
	}
	public void setValue(Boolean value) {
		this.status = value;
	}
public static Actuador random(Integer id, Integer placaId) {

	Long timestamp = System.currentTimeMillis();
	Boolean value = Math.random()<0.5;
	return new Actuador(id, placaId, timestamp, value);
}

}
