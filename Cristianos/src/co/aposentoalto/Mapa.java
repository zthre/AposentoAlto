package co.aposentoalto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.aposentoalto.asyntask.UpdateIglesias;
import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Conf;
import co.aposentoalto.sql.objetos.Iglesias;
import co.aposentoalto.util.Util;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class Mapa extends FragmentActivity implements OnMarkerDragListener,
		OnMarkerClickListener {

	GoogleMap mapa;
	Util u;
	String estado = "";

	private LocationManager locManager;
	private Location loc;

	private Map<Marker, String> markerMap = new HashMap<Marker, String>();
	// private LocationListener locListener;
	CameraUpdate camUpd;
	CameraPosition camPos;
	double lat = 4.65;
	double lng = -74.10;
	int zoom = 14;

	List<Iglesias> listaIglesias;
	Conexion conexion;

	String gpsActivo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		conexion = new Conexion(this);
		listaIglesias = conexion.consultaIglesias();

		u = new Util(this);
		mapa = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Zoom
		mapa.getUiSettings().setZoomControlsEnabled(true);

		/*
		 * Mi locacion
		 */
		mapa.setMyLocationEnabled(true);
		mapa.getUiSettings().setMyLocationButtonEnabled(true);

		// brujula
		mapa.getUiSettings().setCompassEnabled(true);

		mapa.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

			@Override
			public boolean onMyLocationButtonClick() {
				mapa.clear();
				loc = mapa.getMyLocation();
				addIglesias();
				return false;
			}
		});

		mapa.setOnMarkerDragListener(this);
		mapa.setOnMarkerClickListener(this);

		addIglesias();

		comenzarLocalizacion();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main_mapa, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, Inicio.class);
			startActivity(i);
			finish();
			return true;
		case R.id.action_plus_iglesia:
			newIglesia();
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

	public void newIglesia() {

		LinearLayout l = (LinearLayout) findViewById(R.id.formaNewIglesia);
		l.setVisibility(View.VISIBLE);

		mapa.clear();

		addNewIglesia(loc.getLatitude(), loc.getLongitude());

		TextView eId = (TextView) findViewById(R.id.TextId);
		eId.setText("0");

		moveMarker(loc.getLatitude(), loc.getLongitude());
		LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
		posicionarCamara(pos, zoom + 2);
		estado = "nuevo";

	}

	public void cancelarNewIglesia(View v) {

		LinearLayout l = (LinearLayout) findViewById(R.id.formaNewIglesia);
		l.setVisibility(View.INVISIBLE);

		Button btn = (Button) findViewById(R.id.btn_save);
		btn.setText(getString(R.string.save).toString());

		Button bdelete = (Button) findViewById(R.id.btn_delete);
		bdelete.setVisibility(View.INVISIBLE);

		limpiaForma();

		mapa.clear();
		addIglesias();

		comenzarLocalizacion();
	}

	public void guardar(View v) {
		CUD("no");
	}

	public void borrar(View v) {
		estado = "borra";

		AlertDialog.Builder alert = new AlertDialog.Builder(Mapa.this);
		alert.setTitle("Por que deseas Borrar?");
		alert.setMessage("Ingrese el motivo por el cual deseas borrarlo."); // Message
		// Set an EditText view to get user
		// input
		final EditText input = new EditText(Mapa.this);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		input.setSingleLine(true);
		input.requestFocus();
		alert.setView(input);

		alert.setPositiveButton("Enviar",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						String coment = input.getEditableText().toString();
						if (coment.equals("")) {
							u.dialog(
									android.R.drawable.ic_dialog_alert,
									"Error !!!",
									"Debe existir una razón para poder borrar.",
									"OK");
						} else {
							CUD(coment);
						}

					}
				}); // End of
					// alert.setPositiveButton
		alert.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.create().show();

	}

	private Marker addNewIglesia(double lat, double lng) {

		Marker mark = mapa.addMarker(new MarkerOptions().position(
				new LatLng(lat, lng)).draggable(true));
		// mark.set
		return mark;
	}

	private void comenzarLocalizacion() {
		// Obtenemos una referencia al LocationManager
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Trae la mejor localizacion
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locManager.getBestProvider(cri, true);
		loc = locManager.getLastKnownLocation(provider);

		if (loc == null) {
			List<String> listaProviders = locManager.getAllProviders();
			for (String p : listaProviders) {
				loc = locManager.getLastKnownLocation(p);
				if (loc != null) {
					provider = p;
					break;
				}
			}
		}
		// Obtenemos la última posición conocida
		// Location loc = locManager
		// .getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Mostramos la última posición conocida
		if (loc == null) {
			u.dialog(android.R.drawable.ic_dialog_alert, "Advertencia",
					"No hay localizacion", "OK");
		} else {
			// Mostramos la última posición conocida
			mostrarPosicion(true, zoom);
		}

		// muestra la posicion cada vez que cambie o cada 3 segundos segin las
		// lineas siguientes
		LocationListener locListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				loc = location;
				mostrarPosicion(false, zoom);
				;
			}

			public void onProviderDisabled(String provider) {
				gpsActivo = "NO";
			}

			public void onProviderEnabled(String provider) {
				gpsActivo = "SI";
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				gpsActivo = "SI";
			}
		};
		// actualiza cada segundo la camara
		locManager.requestLocationUpdates(provider, 20000, 0, locListener);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(Mapa.this, Inicio.class);
		startActivity(i);
		finish();
	}

	private void mostrarPosicion(boolean camera, int z) {
		LatLng pos = null;

		if (loc != null) {
			pos = new LatLng(loc.getLatitude(), loc.getLongitude());

			if (camera) {
				posicionarCamara(pos, z);
			}
			mapa.clear();
			addIglesias();
		}
	}

	public void posicionarCamara(LatLng pos, int z) {
		camPos = new CameraPosition.Builder().target(pos).zoom(z).build();
		camUpd = CameraUpdateFactory.newCameraPosition(camPos);
		mapa.animateCamera(camUpd);
	}

	private void addIglesias() {
		BitmapDescriptor b = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_map);

		for (Iglesias iglesia : listaIglesias) {
			double lat = Double.parseDouble(iglesia.lat);
			double lng = Double.parseDouble(iglesia.lng);

			MarkerOptions m = new MarkerOptions()
					.position(new LatLng(lat, lng)).title(iglesia.name)
					.snippet(iglesia.direccion).icon(b).draggable(true);
			Marker marker = mapa.addMarker(m);
			markerMap.put(marker, iglesia.id);
		}
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		moveMarker(marker.getPosition().latitude, marker.getPosition().longitude);
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		moveMarker(marker.getPosition().latitude, marker.getPosition().longitude);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		moveMarker(marker.getPosition().latitude, marker.getPosition().longitude);
	}

	public void moveMarker(double lat, double lon) {
		TextView eLat = (TextView) findViewById(R.id.textNewLat);
		TextView eLon = (TextView) findViewById(R.id.textNewLon);
		eLat.setText(lat + "");
		eLon.setText(lon + "");
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		if (!marker.getTitle().equals(getString(R.string.usted))) {

			LatLng pos = new LatLng(marker.getPosition().latitude,
					marker.getPosition().longitude);
			posicionarCamara(pos, zoom + 2);

			String id = markerMap.get(marker);
			TextView eId = (TextView) findViewById(R.id.TextId);
			eId.setText(id);

			LinearLayout l = (LinearLayout) findViewById(R.id.formaNewIglesia);
			l.setVisibility(View.VISIBLE);

			Button bdelete = (Button) findViewById(R.id.btn_delete);
			bdelete.setVisibility(View.VISIBLE);

			Button btn = (Button) findViewById(R.id.btn_save);
			btn.setText(getString(R.string.update).toString());

			moveMarker(marker.getPosition().latitude, marker.getPosition().longitude);
			EditText eNombre = (EditText) findViewById(R.id.EditTextNewName);
			eNombre.setText(marker.getTitle());
			EditText eDireccion = (EditText) findViewById(R.id.EditTextNewDireccion);
			eDireccion.setText(marker.getSnippet());
			estado = "modifica";
		}
		return false;
	}

	public void limpiaForma() {
		TextView eLat = (TextView) findViewById(R.id.textNewLat);
		TextView eLon = (TextView) findViewById(R.id.textNewLon);
		EditText eName = (EditText) findViewById(R.id.EditTextNewName);
		EditText eDireccion = (EditText) findViewById(R.id.EditTextNewDireccion);
		eLat.setText("");
		eLon.setText("");
		eName.setText("");
		eDireccion.setText("");
	}

	// crea, actualiza y elimina
	public void CUD(String comentarios) {
		Conf co = new Conf();
		co.id = "IMEI";
		co = conexion.consultaConf(co).get(0);

		TextView eId = (TextView) findViewById(R.id.TextId);
		TextView eLat = (TextView) findViewById(R.id.textNewLat);
		TextView eLon = (TextView) findViewById(R.id.textNewLon);
		EditText eNombre = (EditText) findViewById(R.id.EditTextNewName);
		EditText eDireccion = (EditText) findViewById(R.id.EditTextNewDireccion);

		if(eLat.getText().toString().equals("") || eLon.getText().toString().equals("") || 
				eNombre.getText().toString().equals("") || eDireccion.getText().toString().equals("")){
			u.dialog(android.R.drawable.ic_dialog_alert, "Error", 
					"Hay campos vacios por favor verifique.", "OK");
		}else{
			String link = "";
			link = "http://social.aposentoalto.co/cristianos/cu_Iglesias.php?";
			link += "id=" + u.convertirURL(eId.getText().toString());
			link += "&usuarios_id=" + u.convertirURL(co.desc);
			link += "&lat=" + u.convertirURL(eLat.getText().toString());
			link += "&lng=" + u.convertirURL(eLon.getText().toString());
			link += "&name=" + u.convertirURL(eNombre.getText().toString());
			link += "&direccion=" + u.convertirURL(eDireccion.getText().toString());
			link += "&comentarios=" + u.convertirURL(comentarios);
			link += "&estado_propuesto=" + estado;
	
			UpdateIglesias u = new UpdateIglesias(this);
			u.execute(link);
		}
	}

}
