package co.aposentoalto.sql.objetos;

public class Iglesias {
	public String TABLA="iglesias";
	public String id = "id";
	public String lat = "lat";
	public String lng = "lng";
	public String name = "name";
	public String direccion = "direccion";
	
	public Iglesias(){
	}

	public String creaTabla(){
		String c = "CREATE TABLE "+TABLA+" "+
				"("+
				id+" TEXT, "+
				lat+" TEXT, " +
				lng+" TEXT, " +
				name+" TEXT, " +
				direccion+" TEXT, " +
				"PRIMARY KEY("+id+")"+
			");";
		return c;
	}
	public String borraTabla(){
		return "DROP TABLE IF EXISTS "+TABLA+";";
	}
	public String borraDatos(){
		return "DELETE FROM "+TABLA+";";
	}
}
