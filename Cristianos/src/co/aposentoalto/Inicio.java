package co.aposentoalto;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import co.aposentoalto.adapter.MenuAdapter;
import co.aposentoalto.fragment.ContenidoFragment;

import com.google.analytics.tracking.android.EasyTracker;

public class Inicio extends FragmentActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPaginaTitles;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio);

		mDrawerTitle = "Menú";
		mTitle = getTitle();
		
		String[] array  = new String[]{"Perfil"};
		
		mPaginaTitles = array;
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// establecer una sombra personalizado que se superpone a la página
		// principal cuando se abre el cajón
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// configurar la vista de lista del cajón con elementos y haga clic
		// oyente
		mDrawerList.setAdapter(new MenuAdapter(this, array));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* Actividad Actual */
		mDrawerLayout, /* DrawerLayout layout */
		R.drawable.ic_drawer, /*
							 * imagen Cajón de navegación para reemplazar 'Up'
							 * caret
							 */
		R.string.drawer_open, /* "open drawer" description de acesso */
		R.string.drawer_close /* "close drawer" description de acesso */
		) {
			// al cerrar el menu
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // llamado a onPrepareOptionsMenu()
			}

			// al abrir el menu
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // llamado a onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Comienza con la portada o item (0) del array
		if (savedInstanceState == null) {
			selectItem(1);
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Si el cajón nav está abierto, ocultar los elementos de acción
		// relacionados con la vista de contenido
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.main_map).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		Intent i;
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.main_map:
			i = new Intent(this, Mapa.class);
			startActivity(i);
			finish();
			return true;
		case R.id.main_preguntas:
			i = new Intent(this, Preguntas.class);
			startActivity(i);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* Click en Cajon de Navegacion */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		Bundle args = new Bundle();
		// actualizar el contenido principal de fragmentos de sustitución
		ContenidoFragment fragment = new ContenidoFragment();
		args.putInt("menu", position);// inicia en la pagina 1
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// actualizar el artículo y el título seleccionado, a continuación,
		// cierre el cajón
		mDrawerList.setItemChecked(position, true);
		setTitle(mPaginaTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.inicio, menu);
	// return true;
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
