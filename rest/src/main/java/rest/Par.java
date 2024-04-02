package rest;

public class Par {
	public Integer id;
	public Integer placaId;
	
	public Par (Integer id, Integer placaId) {
		this.id = id;
		this.placaId = placaId;
	}
	public static Par of(Integer  id,Integer placaId) {
		return new Par(id, placaId);
	}
	@Override
	public String toString() {
		return "Par [id=" + id + ", placaId=" + placaId + "]";
	}

}
