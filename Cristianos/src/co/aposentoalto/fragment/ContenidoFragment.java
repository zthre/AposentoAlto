package co.aposentoalto.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import co.aposentoalto.R;
import co.aposentoalto.adapter.AdapterPerfil;

public class ContenidoFragment extends Fragment {
	View rootView;
	Activity mActivity;
	int numeroPagina;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		numeroPagina = getArguments().getInt("menu");// numero de pagina

		rootView = inflater.inflate(R.layout.fragment_inicio, container, false);
		mActivity = (Activity) rootView.getContext();

		ListView l = (ListView) rootView.findViewById(R.id.list);
		
		switch (numeroPagina) {
		case 0:			
			l.setAdapter(new AdapterPerfil(mActivity));
			break;
		default:
			break;
		}
		
		return rootView;
	}

}
