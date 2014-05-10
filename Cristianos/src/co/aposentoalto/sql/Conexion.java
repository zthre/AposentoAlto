package co.aposentoalto.sql;

import java.util.List;

import android.content.Context;
import co.aposentoalto.sql.objetos.Conf;
import co.aposentoalto.sql.objetos.Iglesias;

public class Conexion extends UtilidadDB{

	IglesiasDB iglesiasDB;
	ConfDB confDB;
	
	public Conexion(Context ctx) {
		setContexto(ctx);
		open();
		iglesiasDB = new IglesiasDB();
		confDB = new ConfDB();
	}

	/*
	 * CONSULTAS
	 * */
	public List<Iglesias> consultaIglesias(){
		return iglesiasDB.consultaAll(getDb());
	}
	public List<Conf> consultaConf(Conf conf){
		return confDB.consultaAll(getDb(),conf);
	}
	
	

	/*
	 * INSERTS
	 * */
	public void insertaIglesia(Iglesias i){
		iglesiasDB.insert(getDb(), i);
	}
	public void insertaConf(Conf c){
		confDB.insert(getDb(), c);
	}

	/*
	 * MODIFICACIONES
	 * */
	public void modificaConf(Conf c){
		confDB.actualizaKey(getDb(), c);
	}
	
	/*
	 * BORRADO
	 * */
	public void borraIglesias(){
		iglesiasDB.borraTODO(getDb());
	}
	
	
	
}
