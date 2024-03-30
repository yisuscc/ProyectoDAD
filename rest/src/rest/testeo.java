package rest;

public class testeo {

	public static void main(String[] args) {
		AglutinadorPlacaSensorActuador agpsa = AglutinadorPlacaSensorActuador.getRandomData(6);
//System.out.println(agpsa.toString());
//System.out.println(agpsa.getSensoresPlaca(0).toString());
//System.out.println(agpsa.getLastSensoresPlaca(2));
//	System.out.println(agpsa.getLastSensor(0, 1).toString());
	Actuador act = new Actuador(0, 0, System.currentTimeMillis(), true);
		//System.out.println(act.toString());
	//agpsa.addActuador(act);
//System.out.println(agpsa.getLastActuador(0, 0));
//System.out.println(agpsa.getActuadoresPlaca(0));
//System.out.println(agpsa.getLastActuadoresPlaca(0));
System.out.println(agpsa.getLastSensoresList(0));
System.out.println(agpsa.getLastActuadoresList(0));
	}

}
