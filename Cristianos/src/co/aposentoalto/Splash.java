package co.aposentoalto;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import co.aposentoalto.asyntask.ActualizaIglesias;

import com.google.analytics.tracking.android.EasyTracker;

public class Splash extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Pone la pantalla
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			public void run() {
				ActualizaIglesias a = new ActualizaIglesias(Splash.this, Login.class);
				a.actualizar();
			}
		}, 300);
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
