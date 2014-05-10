package co.aposentoalto.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import co.aposentoalto.sql.objetos.Conf;

// Clase intermediaria para acceder y manipular los datos de la base de datos.
public class ConfDB {

	Conf confTabla;

	/**
	 * Constructores y metodos.
	 */

	public ConfDB() {
		confTabla = new Conf();
	}

	/**
	 * Obtiene todos los registros de la tabla notas.
	 * 
	 * @return Devuelve un cursor con los registros obtenidos.
	 */
	public List<Conf> consultaAll(SQLiteDatabase db, Conf conf) {

		Cursor c = db.query(
				confTabla.TABLA,
				new String[] { confTabla.id, 
						confTabla.desc}, confTabla.id+"='"+conf.id+"'", null,
				null, null, null);

		List<Conf> lista = new ArrayList<Conf>();
		Conf co;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			co = new Conf();
			co.id = c.getString(0);
			co.desc = c.getString(1);
			lista.add(co);
		}
		return lista;
	}
	
	public long insert(SQLiteDatabase db, Conf coIN) {
		// Variable utilizada para enviar los datos al método insert.
		ContentValues registro = new ContentValues();

		// Agrega los datos.
		registro.put(confTabla.id, coIN.id);

		registro.put(confTabla.desc, coIN.desc);
		
		db.insert(confTabla.TABLA, null, registro);
		// Inserta el registro y devuelve el resultado.
		return 1;
	}
	
	public int actualizaKey(SQLiteDatabase db, Conf coIN) {
		// Variable utilizada para enviar los datos al metodo update.
		ContentValues registro = new ContentValues();
		// Agrega los datos.registro.put(pagina.getPath(), paginaIN.getPath());;
		registro.put(confTabla.desc, coIN.desc);

		db.update(
				confTabla.TABLA,
				registro,
				confTabla.id + " = '" + coIN.id
						+ "'", null);
		// Inserta el registro y devuelve el resultado.
		return 1;
	}

	public int borraTODO(SQLiteDatabase db) {
		db.execSQL(confTabla.borraDatos());
		return 1;
	}

}
