package co.aposentoalto;

import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Conf;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Notificaciones extends Activity {
	private int NOTIF_ALERTA_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notificacion);

		NotificationManager mNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		TextView t = (TextView) findViewById(R.id.notificacion_mensaje);
		Button b = (Button) findViewById(R.id.notificacion_boton);

		Conexion conexion = new Conexion(getApplicationContext());
		Conf conf = new Conf();
		conf.id = "notificacion";
		conf = conexion.consultaConf(conf).get(0);
		String info = conf.desc;

		conf = new Conf();
		conf.id = "notificacion_mensaje";
		conf = conexion.consultaConf(conf).get(0);
		String text = conf.desc;
		conexion.close();

		mNotification.cancel(NOTIF_ALERTA_ID);

		t.setText(text);
		if (info.equals("1")) {
			Intent in = new Intent(Intent.ACTION_VIEW);
			in.setData(Uri.parse("market://details?id=com.cristianos"));
			startActivity(in);
			finish();
		} else {
			b.setText("Entrar");
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent in = new Intent(Notificaciones.this, Splash.class);
					startActivity(in);
					finish();
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
}
