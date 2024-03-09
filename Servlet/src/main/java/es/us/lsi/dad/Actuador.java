/**
 * 
 */
package es.us.lsi.dad;

import java.util.Objects;

/**
 * 
 */
public class Actuador {
	private String id;
	private Long timestamp;
	private String status;
	private boolean encendido;
	@Override
	public int hashCode() {
		return Objects.hash(encendido, id, status, timestamp);
	}
	@Override
	public String toString() {
		return "Actuador [id=" + id + ", timestamp=" + timestamp + ", status=" + status + ", encendido=" + encendido
				+ "]";
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
		return encendido == other.encendido && Objects.equals(id, other.id) && Objects.equals(status, other.status)
				&& Objects.equals(timestamp, other.timestamp);
	}
	public Actuador(String id, Long timestamp, String status, boolean encendido) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.status = status;
		this.encendido = encendido;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isEncendido() {
		return encendido;
	}
	public void setEncendido(boolean encendido) {
		this.encendido = encendido;
	}
}
