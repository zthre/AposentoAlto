package co.aposentoalto.sql.objetos;


public class Conf {
	public String TABLA="conf";
	public String id = "id";
	public String desc = "desc";
	
	public Conf(){
	}

	public String creaTabla(){
		String c = "CREATE TABLE "+TABLA+" "+
				"("+
				id+" TEXT, "+
				desc+" TEXT, " +
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
