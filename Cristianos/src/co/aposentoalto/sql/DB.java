package co.aposentoalto.sql;

import java.io.IOException;

import co.aposentoalto.sql.objetos.Conf;
import co.aposentoalto.sql.objetos.Iglesias;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "aposentoalto.db";
	private static final int DATABASE_VERSION = 1;// Version 1 =
													// DATABASE_VERSION = 1
	private Iglesias iglesiaTabla;
	private Conf confTabla;

	/**
	 * @param context
	 * @throws IOException
	 */
	public DB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		iglesiaTabla = new Iglesias();
		confTabla = new Conf();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(iglesiaTabla.creaTabla());
			db.execSQL(iglesiaTabla.borraDatos());
			datosIglesias(db);
			db.execSQL(confTabla.creaTabla());
			db.execSQL(confTabla.borraDatos());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void datosIglesias(SQLiteDatabase db) {
		String insert = "INSERT INTO " + iglesiaTabla.TABLA + " ("
				+ iglesiaTabla.id + ", " + iglesiaTabla.lat + ", "
				+ iglesiaTabla.lng + ", " + iglesiaTabla.name + ", "
				+ iglesiaTabla.direccion + ") ";
		String sql = "";
		sql = insert
				+ "VALUES "
				+ "('1', '4.617464','-74.133446','Aposento Alto Américas','Carrera 70B  No. 22-30 Sur');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('2', '4.704236','-74.03531','Aposento Alto Calle 126','Calle 126  No. 7c-21');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('3', '4.726091','-74.053452','Aposento Alto Calle 140','Carrera 46  No. 143-49');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('4', '4.702065','-74.098277','Aposento Alto Calle 80','Calle 80 Bis No.   83-09');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('5', '4.693286','-74.077313','Aposento Alto LYE','Calle 98  No. 69-22');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('6', '4.591098','-74.195942','Aposento Alto CAZUCA - NUEVO COLON','Transversal 6  No. 3-27 Soacha');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('7', '4.734987','-74.016159','Aposento Alto CERRO NORTE','Carrera 1 con Calle 161');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('8', '4.858744','-74.057695','Aposento Alto CHIA','Carrera 7 No 10-72 Chia');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('9', '4.699755','-74.123962','Aposento Alto ENGATIVA','Av.Engativa Calle 61 No.105 i- 40');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('10', '4.674696','-74.083316','Aposento Alto JJ VARGAS','Av. Calle 68  No. 65-10 Piso 2');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('11', '4.622218','-74.165778','Aposento Alto KENNEDY','Calle 42G Sur No. 79G-40');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('12', '4.599151','-74.13358','Aposento Alto MUZU','Carrera 50 C No. 41 - 07 Sur');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('13', '4.66897','-74.110476','Aposento Alto NORMANDIA','Carrera 74 A No. 49A-34');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('14', '4.681139','-74.084115','Aposento Alto NOR-OCCIDENTE','Carrera 68 C No. 74 - 93');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('15', '4.740897','-74.092454','Aposento Alto SUBA','Carrera 101 A No. 140-36');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('16', '4.576366','-74.101399','Aposento Alto SURORIENTE','Calle 26 Sur  No. 13-26');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('17', '4.650409','-74.0709299','Aposento Alto TEUSAQUILLO','Carrera 21  No. 62-74');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('18', '4.589633','-74.139894','Aposento Alto VENECIA','Diagonal 45 Sur  No. 48-58');";
		db.execSQL(sql);
		sql = insert
				+ "VALUES "
				+ "('19', '4.745337','-74.115357','Aposento Alto BERLIN','Calle 135 A No.145-71 Suba');";
		db.execSQL(sql);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion) {
	}

}