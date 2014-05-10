package co.aposentoalto.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import co.aposentoalto.sql.objetos.Iglesias;

// Clase intermediaria para acceder y manipular los datos de la base de datos.
public class IglesiasDB {

	Iglesias iglesiaTabla;

	/**
	 * Constructores y metodos.
	 */

	public IglesiasDB() {
		iglesiaTabla = new Iglesias();
	}

	public long insert(SQLiteDatabase db, Iglesias iglesiasIN) {
		// Variable utilizada para enviar los datos al método insert.
		ContentValues registro = new ContentValues();

		// Agrega los datos.
		registro.put(iglesiaTabla.id, iglesiasIN.id);
		registro.put(iglesiaTabla.lat, iglesiasIN.lat);
		registro.put(iglesiaTabla.lng, iglesiasIN.lng);
		registro.put(iglesiaTabla.name, iglesiasIN.name);
		registro.put(iglesiaTabla.direccion, iglesiasIN.direccion);
		
		db.insert(iglesiaTabla.TABLA, null, registro);
		// Inserta el registro y devuelve el resultado.
		return 1;
	}
	
	public List<Iglesias> consultaAll(SQLiteDatabase db) {

		Cursor c = db.query(
				iglesiaTabla.TABLA,
				new String[] { iglesiaTabla.id, 
						iglesiaTabla.lat,
						iglesiaTabla.lng,
						iglesiaTabla.name,
						iglesiaTabla.direccion }, null, null,
				null, null, null);

		List<Iglesias> lista = new ArrayList<Iglesias>();
		Iglesias o;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			o = new Iglesias();
			o.id = c.getString(0);
			o.lat = c.getString(1);
			o.lng = c.getString(2);
			o.name = c.getString(3);
			o.direccion = c.getString(4);
			lista.add(o);
		}
		return lista;
	}

	public int borraTODO(SQLiteDatabase db) {
		db.execSQL(iglesiaTabla.borraDatos());
		return 1;
	}

}
