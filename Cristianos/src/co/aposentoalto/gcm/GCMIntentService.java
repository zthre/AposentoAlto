package co.aposentoalto.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import co.aposentoalto.Notificaciones;
import co.aposentoalto.R;
import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Conf;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {
	private int NOTIF_ALERTA_ID = 1;

	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		Bundle extras = intent.getExtras();

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				mostrarNotification(extras);
			}
		}

		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void mostrarNotification(Bundle extras) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(extras.getString("contentTitle"))
				.setContentText(extras.getString("contentText"))
				.setTicker(extras.getString("tickerText"))
				.setContentInfo(extras.getString("info"));

		/**
		 * Graba en la notificacion los mensajes
		 * */
		Conexion conexion = new Conexion(getApplicationContext());
		Conf conf = new Conf();
		conf.id = "notificacion";
		conf.desc = extras.getString("info");
		if(conexion.consultaConf(conf).size()>0){
			conexion.modificaConf(conf);
		}else{
			conexion.insertaConf(conf);
		}

		conf.id = "notificacion_mensaje";
		conf.desc = extras.getString("contentText");
		if(conexion.consultaConf(conf).size()>0){
			conexion.modificaConf(conf);
		}else{
			conexion.insertaConf(conf);
		}
		conexion.close();
		
		Intent i = new Intent(this, Notificaciones.class);
		PendingIntent contIntent = PendingIntent
				.getActivity(this, 0, i, 0);
		mBuilder.setContentIntent(contIntent);

		mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
	}
}