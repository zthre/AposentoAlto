package co.aposentoalto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Conf;
import co.aposentoalto.util.Util;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

public class Login extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog connectionProgressDialog;
	private PlusClient plusClient;
	private ConnectionResult connectionResult;

	private SignInButton btnSignIn;
	private String IMEI;
	private Util u;
	private String link;

	// GCM
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static final String EXTRA_MESSAGE = "message";
	private static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
	private static final String PROPERTY_USER = "user";

	public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;

	static final String TAG = "GCMDemo";

	private Context context;
	private String regid;
	private GoogleCloudMessaging gcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		u = new Util(this);

		btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);

		plusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/ListenActivity").build();

		connectionProgressDialog = new ProgressDialog(this);
		connectionProgressDialog.setMessage("Conectando...");

		btnSignIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (!plusClient.isConnected()) {
					if (connectionResult == null) {
						connectionProgressDialog.show();
					} else {
						try {
							connectionResult.startResolutionForResult(
									Login.this, REQUEST_CODE_RESOLVE_ERR);
						} catch (Exception e) {
							connectionResult = null;
							plusClient.connect();
						}
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.inicio, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		// Cerrar Sesión
		case R.id.action_sign_out:
			if (plusClient.isConnected()) {
				plusClient.clearDefaultAccount();
				plusClient.disconnect();
				plusClient.connect();
				Toast.makeText(Login.this, "Sesión Cerrada.", Toast.LENGTH_LONG)
						.show();
			}

			return true;
			// Revocar permisos a la aplicación
		case R.id.action_revoke_access:
			if (plusClient.isConnected()) {
				plusClient.clearDefaultAccount();
				plusClient
						.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
							@Override
							public void onAccessRevoked(ConnectionResult status) {
								Toast.makeText(Login.this,
										"Acceso App Revocado",
										Toast.LENGTH_LONG).show();
							}
						});
			}

			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		plusClient.connect();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();
		plusClient.disconnect();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (connectionProgressDialog.isShowing()) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					plusClient.connect();
				}
			}
		}

		connectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {

		connectionProgressDialog.dismiss();

		String tipo = "IMEI";
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = tm.getDeviceId();
		if (IMEI == null || IMEI.equals("")) {
			IMEI = tm.getSimSerialNumber();
			if (IMEI == null || IMEI.equals("")) {
				IMEI = plusClient.getAccountName();
				tipo = "MAIL";
			} else {
				tipo = "SERIAL";
			}
		}

		Conexion c = new Conexion(this);
		Conf co = new Conf();
		co.id = "IMEI";
		co.desc = IMEI;

		List<Conf> list = c.consultaConf(co);
		if (list.size() > 0) {
			c.modificaConf(co);
		} else {
			c.insertaConf(co);
		}
		c.close();

		String fecha_nac = plusClient.getCurrentPerson().getBirthday();
		if (fecha_nac == null || fecha_nac.equals("null")
				|| fecha_nac.equals("")) {
			fecha_nac = "1900-01-01";
		}

		link = "http://social.aposentoalto.co/cristianos/gcm_registra.php?";
		link += "id=" + IMEI;
		link += "&name="
				+ u.convertirURL(plusClient.getCurrentPerson().getDisplayName());
		link += "&nick="
				+ u.convertirURL(plusClient.getCurrentPerson().getNickname());
		link += "&mail=" + u.convertirURL(plusClient.getAccountName());
		link += "&version="
				+ u.convertirURL(getText(R.string.versionName).toString());
		link += "&nacimiento=" + u.convertirURL(fecha_nac);
		link += "&genero="
				+ u.convertirURL(plusClient.getCurrentPerson().getGender() + "");
		link += "&lenguaje="
				+ u.convertirURL(plusClient.getCurrentPerson().getLanguage());
		link += "&url="
				+ u.convertirURL(plusClient.getCurrentPerson().getUrl());
		link += "&tipo=" + tipo;

		context = getApplicationContext();

		// Chequemos si está instalado Google Play Services
		if (checkPlayServices()) {

			gcm = GoogleCloudMessaging.getInstance(Login.this);
			// Obtenemos el Registration ID guardado
			regid = getRegistrationId(context);
			// Si no disponemos de Registration ID comenzamos el
			// registro
			if (regid.equals("")) {
				TareaRegistroGCM tarea = new TareaRegistroGCM();
				tarea.execute(IMEI.toString(), "no");
			} else {
				link += "&regid=" + u.convertirURL(regid);
				TareaRegistroGCM tarea = new TareaRegistroGCM();
				tarea.execute(IMEI.toString(), "si");
			}
		} else {
			Toast.makeText(Login.this,
					"No se ha encontrado Google Play Services.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Desconectado!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {

		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			connectionResult = null;
			plusClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(Login.this, "Dispositivo no soportado.",
						Toast.LENGTH_LONG).show();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) {

		SharedPreferences prefs = getSharedPreferences(
				Login.class.getSimpleName(), Context.MODE_PRIVATE);

		String registrationId = prefs.getString(PROPERTY_REG_ID, "");

		if (registrationId.length() == 0) {
			return "";
		}

		String registeredUser = prefs.getString(PROPERTY_USER, "user");

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);

		long expirationTime = prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);
		int currentVersion = getAppVersion(context);

		if (registeredVersion != currentVersion) {
			return "";
		} else if (System.currentTimeMillis() > expirationTime) {
			return "";
		} else if (!IMEI.equals(registeredUser)) {
			return "";
		}

		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);

			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Error al obtener versión: " + e);
		}
	}

	private class TareaRegistroGCM extends AsyncTask<String, Integer, String> {

		private ProgressDialog dialogProgress;

		protected void onPreExecute() {
			dialogProgress = new ProgressDialog(Login.this);
			dialogProgress.setMessage("Autenticando ...");
			dialogProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialogProgress.setCancelable(false);
			dialogProgress.show(); // Mostramos el diálogo antes de comenzar
		}

		@Override
		protected String doInBackground(String... params) {
			String msg = "";
			try {
				if (params[1].equals("no")) {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}

					// Nos registramos en los servidores de GCM
					regid = gcm.register(getString(R.string.sender_id)
							.toString());
				}

				link += "&regid=" + u.convertirURL(regid);
				// Nos registramos en nuestro servidor
				boolean registrado = registroServidor(link);

				// Guardamos los datos del registro
				if (registrado) {
					setRegistrationId(context, params[0], regid);
					Intent i = new Intent(Login.this, Inicio.class);
					startActivity(i);
					publishProgress(100);
					finish();
				} else {
					publishProgress(-100);
				}
			} catch (IOException ex) {
				publishProgress(-100);
			}

			return msg;
		}

		protected void onProgressUpdate(Integer... values) {
			dialogProgress.dismiss();
			if (values[0] == -100) {
				u.dialogOkClass(
						android.R.drawable.ic_dialog_alert,
						"Error !!!",
						"Debe pudimos conectarnos con el servidor vuelve a intentarlo.",
						"OK",Login.class);
			}
		}
	}

	private void setRegistrationId(Context context, String user, String regId) {
		SharedPreferences prefs = getSharedPreferences(
				Login.class.getSimpleName(), Context.MODE_PRIVATE);

		int appVersion = getAppVersion(context);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_USER, user);
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.putLong(PROPERTY_EXPIRATION_TIME, System.currentTimeMillis()
				+ EXPIRATION_TIME_MS);

		editor.commit();
	}

	private boolean registroServidor(String url) {
		boolean reg = false;

		String SERVER_URL = url;

		// Create an HTTP client
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(SERVER_URL);

		// Perform the request and check the status code
		HttpResponse response;
		try {
			response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream instream = entity.getContent();
				String result = u.convertStreamToString(instream);
				if (result.startsWith("OK")) {
					reg = true;

					String[] arrayValor = result.split("-");

					Conexion c = new Conexion(Login.this);
					Conf co = new Conf();
					co.id = "name";
					co.desc = arrayValor[1];

					List<Conf> list = c.consultaConf(co);
					if (list.size() > 0) {
						c.modificaConf(co);
					} else {
						c.insertaConf(co);
					}

					c = new Conexion(Login.this);
					co = new Conf();
					co.id = "puntos";
					co.desc = arrayValor[2];

					list = c.consultaConf(co);
					if (list.size() > 0) {
						c.modificaConf(co);
					} else {
						c.insertaConf(co);
					}

					c = new Conexion(Login.this);
					co = new Conf();
					co.id = "img";
					co.desc = plusClient.getCurrentPerson().getImage().getUrl();

					list = c.consultaConf(co);
					if (list.size() > 0) {
						c.modificaConf(co);
					} else {
						c.insertaConf(co);
					}

					c.close();

				} else {
					reg = false;
				}
				instream.close();
			}

		} catch (Exception e) {
			reg = false;
		}
		return reg;
	}

}
