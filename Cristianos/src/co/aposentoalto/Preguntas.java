package co.aposentoalto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import co.aposentoalto.asyntask.UpdatePreguntas;
import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Conf;
import co.aposentoalto.util.Util;

import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("NewApi")
public class Preguntas extends Activity{
	

	EditText editTextPregunta;
	EditText editTextRespuesta1;
	EditText editTextRespuesta2;
	EditText editTextRespuesta3;
	EditText editTextRespuesta4;

	RadioButton radioButton1;
	RadioButton radioButton2;
	RadioButton radioButton3;
	RadioButton radioButton4;
	Util u;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preguntas);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		u = new Util(this);
		
		editTextPregunta = (EditText) findViewById(R.id.editTextPregunta);
		editTextRespuesta1 = (EditText) findViewById(R.id.EditTextRespuesta1);
		editTextRespuesta2 = (EditText) findViewById(R.id.EditTextRespuesta2);
		editTextRespuesta3 = (EditText) findViewById(R.id.EditTextRespuesta3);
		editTextRespuesta4 = (EditText) findViewById(R.id.EditTextRespuesta4);

		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
		radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
			
	}
	
	public void enviar(View v){
		if(editTextPregunta.getText().toString().equals("") ||
				editTextRespuesta1.getText().toString().equals("") ||
				editTextRespuesta2.getText().toString().equals("") ||
				editTextRespuesta3.getText().toString().equals("") ||
				editTextRespuesta4.getText().toString().equals("")){
			u.dialog(android.R.drawable.ic_dialog_alert, "Alerta!!!","No debe haber campos vacios.", "OK");
		}else{
			String correcta = "0";
			if(radioButton1.isChecked()){
				correcta = "1";
			}else if(radioButton2.isChecked()){
				correcta = "2";
			}else if(radioButton3.isChecked()){
				correcta = "3";
			}else if(radioButton4.isChecked()){
				correcta = "4";
			}
			if(correcta.equals("0")){
				u.dialog(android.R.drawable.ic_dialog_alert, "Alerta!!!","Una de las respuestas debe ser corrrecta.", "OK");
			}else{
				Conexion conexion = new Conexion(Preguntas.this);
				Conf co = new Conf();
				co.id = "IMEI";
				co = conexion.consultaConf(co).get(0);
				
				String link = "";
				link = "http://social.aposentoalto.co/cristianos/cu_Preguntas.php?";
				link += "&usuarios_id=" + u.convertirURL(co.desc);
				link += "&pregunta=" + u.convertirURL(editTextPregunta.getText().toString());
				link += "&correcta=" + u.convertirURL(correcta);
				link += "&respuesta1=" + u.convertirURL(editTextRespuesta1.getText().toString());
				link += "&respuesta2=" + u.convertirURL(editTextRespuesta2.getText().toString());
				link += "&respuesta3=" + u.convertirURL(editTextRespuesta3.getText().toString());
				link += "&respuesta4=" + u.convertirURL(editTextRespuesta4.getText().toString());
				
				UpdatePreguntas u = new UpdatePreguntas(Preguntas.this);
				u.execute(link);
			}
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.inicio, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, Inicio.class);
			startActivity(i);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
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