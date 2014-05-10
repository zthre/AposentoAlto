package co.aposentoalto.asyntask;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.RadioButton;
import co.aposentoalto.R;
import co.aposentoalto.util.Util;

public class UpdatePreguntas extends AsyncTask<String, Integer, String> {

	private ProgressDialog dialogProgress;
	private Context ctx;
	Util u;

	public UpdatePreguntas(Context ctx) {
		this.ctx = ctx;
		u = new Util(ctx);
	}

	protected void onPreExecute() {
		dialogProgress = new ProgressDialog(ctx);
		dialogProgress.setTitle("Enviando su Pregunta");
		dialogProgress.setMessage("Un momento por favor...");
		dialogProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialogProgress.setCancelable(false);
		dialogProgress.show(); // Mostramos el diálogo antes de comenzar
	}

	@Override
	protected String doInBackground(String... arrayString) {

		String link = arrayString[0];
		// TODO Auto-generated method stub
		boolean ok = guardaIglesias(link);
		if (ok) {
			publishProgress(100);
		}else{
			publishProgress(-1);
		}
		return null;
	}

	protected void onProgressUpdate(Integer... values) {
		dialogProgress.dismiss();
		if(values[0] == 100){
			u.dialog(android.R.drawable.ic_dialog_info, "Gracias", 
					"Hemos recibido la pregunta con sus respectivas respuestas."
					+ "en 24 horas recibiras tus puntos.", "OK");
			limpiaForma();
		}else{
			u.dialog(android.R.drawable.ic_dialog_alert, "Error", 
					"Se ha detectado un error al momento de actualizar los datos intentalo de nuevo," +
					"en caso de que el error continue escribanos a zthre24@gmail.com.", "OK");
		}
	}

	private boolean guardaIglesias(String url) {
		boolean reg = false;

		String SERVER_URL = url;
		try {

			// Create an HTTP client
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(SERVER_URL);

			// Perform the request and check the status code
			HttpResponse response;
			response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream instream = entity.getContent();
				String result = u.convertStreamToString(instream);
				if (result.startsWith("OK")) {
					reg = true;
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
	
	public void limpiaForma(){
		EditText editTextPregunta = (EditText) ((Activity) ctx).findViewById(R.id.editTextPregunta);
		EditText editTextRespuesta1 = (EditText) ((Activity) ctx).findViewById(R.id.EditTextRespuesta1);
		EditText editTextRespuesta2 = (EditText) ((Activity) ctx).findViewById(R.id.EditTextRespuesta2);
		EditText editTextRespuesta3 = (EditText) ((Activity) ctx).findViewById(R.id.EditTextRespuesta3);
		EditText editTextRespuesta4 = (EditText) ((Activity) ctx).findViewById(R.id.EditTextRespuesta4);
		
		editTextPregunta.setText("");
		editTextRespuesta1.setText("");
		editTextRespuesta2.setText("");
		editTextRespuesta3.setText("");
		editTextRespuesta4.setText("");
		
		RadioButton radioButton1 = (RadioButton) ((Activity) ctx).findViewById(R.id.radioButton1);
		RadioButton radioButton2 = (RadioButton) ((Activity) ctx).findViewById(R.id.radioButton2);
		RadioButton radioButton3 = (RadioButton) ((Activity) ctx).findViewById(R.id.radioButton3);
		RadioButton radioButton4 = (RadioButton) ((Activity) ctx).findViewById(R.id.radioButton4);
		
		radioButton1.setChecked(false);
		radioButton2.setChecked(false);
		radioButton3.setChecked(false);
		radioButton4.setChecked(false);
		
		
	}

}
