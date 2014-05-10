package co.aposentoalto.sql;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// Clase intermediaria para acceder y manipular los datos de la base de datos.
public class UtilidadDB {
	// Para manipular el ayudante de conexión.
	private DB conexion;
	// Para manipular acciones sobre la base de datos.
	private SQLiteDatabase db;
	private Context contexto;
	/*****************
	 * Métodos Creados para manipular la base de datos
	 * **************/
	public void setContexto(Context contexto){
		this.contexto = contexto;
	}

	/**
	 * @return Devuelve un objeto de clase SQLiteDatabase como manipulador de
	 *         labase de datos.
	 * @throws SQLException
	 */
	public SQLiteDatabase open() {
		// Crea un objeto asistente de base de datos.
		conexion = new DB(contexto);
		// Abre la base de datos en modo escritura (lectura permitida).
		setDb(conexion.getWritableDatabase());
		// Devuelve el objeto de tipo DataBaseHelper.
		return getDb();
	}

	/**
	 * Cierra la base de datos.
	 */
	public void close() {
		getDb().close();
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
}
