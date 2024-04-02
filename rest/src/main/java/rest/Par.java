package rest;

public record Par(Integer id, Integer placaId) {
public static Par of(Integer id, Integer placaId) {
	return new Par(id, placaId);
}
}
