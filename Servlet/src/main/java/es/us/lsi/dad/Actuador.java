/**
 * 
 */
package es.us.lsi.dad;

import java.util.Objects;

/**
 * 
 */
public class Actuador {
	private Integer id;
	private Long timestamp;
	private String status; //un mensaje que luego el esp3 decodfica
	private boolean encendido; // true si está encendido, false si no 
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
	public Actuador(Integer id, Long timestamp, String status, boolean encendido) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.status = status;
		this.encendido = encendido;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public static Actuador getActuadorRandomWithId(Integer id ) {
		Long timestamp =  System.currentTimeMillis();
		String status = "deactivated";
		Boolean on = Math.random()<0.5;
		return new Actuador(id, timestamp, status, on);
	}
}
