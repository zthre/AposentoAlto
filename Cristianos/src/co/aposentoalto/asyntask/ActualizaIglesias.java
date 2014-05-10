package co.aposentoalto.asyntask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
import android.os.AsyncTask;
import co.aposentoalto.gson.objetos.GsonIglesias;
import co.aposentoalto.gson.objetos.GsonListaIglesias;
import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Iglesias;
import co.aposentoalto.util.Util;

import com.google.gson.Gson;

public class ActualizaIglesias {
	private Context ctx;
	private ProgressDialog dialog;
	Class<?> clase;
	Util u;

	public ActualizaIglesias(Context ctx, Class<?> cls) {
		this.ctx = ctx;
		this.clase = cls;
		this.u = new Util(ctx);
	}

	public void actualizar() {
		dialog = new ProgressDialog(ctx);
		dialog.setMessage("Estamos Actualizando la información...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		// Realizamos cualquier otra operación necesaria
		// Creamos una nueva instancia y llamamos al método ejecutar
		// pasándole el string.
		String p = "http://social.aposentoalto.co/cristianos/consulta_iglesias.php";
		if(u.isOnline()){
			new LoginFetcher().execute(p);
		}else{
			
		}
	}

	private class LoginFetcher extends AsyncTask<String, Float, Integer> {
		public String SERVER_URL;

		protected void onPreExecute() {
			dialog.show(); // Mostramos el diálogo antes de comenzar
		}

		protected void onProgressUpdate(Float... values) {
		}

		protected void onPostExecute(Integer bytes) {
			dialog.dismiss();
		}

		@Override
		protected Integer doInBackground(String... params) {
			try {
				SERVER_URL = params[0].toString();

				// Create an HTTP client
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(SERVER_URL);

				// Perform the request and check the status code
				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();

					try {
						// Leer la respuesta del servidor y tratar de analizarlo
						// como
						// JSON
						Reader reader = new InputStreamReader(content);

						try {
							GsonListaIglesias listaGsonIglesias = new GsonListaIglesias();
							Gson gson = new Gson();
							listaGsonIglesias = gson.fromJson(reader,
									GsonListaIglesias.class);
							content.close();
							handlePostsList(listaGsonIglesias);
						} catch (Exception e) {
							failedLoadingPosts();
						}
					} catch (Exception ex) {
						failedLoadingPosts();
					}
				} else {
					failedLoadingPosts();
				}
			} catch (Exception ex) {
				failedLoadingPosts();
			}
			return 1;
		}

		private void handlePostsList(final GsonListaIglesias lista) {

			((Activity) ctx).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					if (lista.lista.length > 0) {
						Conexion con = new Conexion(ctx);
						con.borraIglesias();
						
						Iglesias i;
						for(GsonIglesias gi:lista.lista){
							i = new Iglesias();
							i.id = gi.id;
							i.lat = gi.lat;
							i.lng = gi.lng;
							i.name = gi.name;
							i.direccion = gi.direccion;
							con.insertaIglesia(i);
						}
						con.close();
						
						cambiarActividad();
					} else {
						cambiarActividad();
					}
				}
			});
		}
		
		public void cambiarActividad(){
			Intent mainIntent = new Intent(ctx, clase);
			ctx.startActivity(mainIntent);
			((Activity) ctx).finish();
		}

		private void failedLoadingPosts() {
			((Activity) ctx).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					cambiarActividad();
				}
			});
		}
	}

}
