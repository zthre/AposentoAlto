package co.aposentoalto.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.aposentoalto.R;
import co.aposentoalto.asyntask.ImageLoader;
import co.aposentoalto.sql.Conexion;
import co.aposentoalto.sql.objetos.Conf;

public class AdapterPerfil extends BaseAdapter {

	private LayoutInflater lInflater;

	Activity mActivity;
	public ImageLoader imageLoader;

	public AdapterPerfil(Context context) {
		this.lInflater = LayoutInflater.from(context);
		imageLoader = new ImageLoader(context.getApplicationContext());
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int pos) {
		return null;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {

		ViewHolder holder;
		if (view == null) {
			view = lInflater.inflate(R.layout.adapter_perfil, null);
			mActivity = ((Activity) view.getContext());
			holder = new ViewHolder();
			holder.nombrePefil = (TextView) view.findViewById(R.id.nombrePerfil);
			holder.puntosPefil = (TextView) view
					.findViewById(R.id.puntosPerfil);
			holder.imagePerfil = (ImageView) view.findViewById(R.id.imagePerfil);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Conexion c = new Conexion(view.getContext());
		Conf co = new Conf();
		co.id = "name";
		co = c.consultaConf(co).get(0);

		
		Conf coPuntos = new Conf();
		coPuntos.id = "puntos";
		coPuntos = c.consultaConf(coPuntos).get(0);
		
		Conf coImage = new Conf();
		coImage.id = "img";
		coImage = c.consultaConf(coImage).get(0);
		
		
		String url = coImage.desc;
		url= url.replace("sz=50", "sz=200");

		holder.nombrePefil.setText(co.desc);
		holder.puntosPefil.setText(coPuntos.desc);
		
		if (holder.imagePerfil != null) {
			imageLoader.DisplayImage(url, holder.imagePerfil);
		}
		return view;
	}

	static class ViewHolder {
		TextView nombrePefil;
		TextView puntosPefil;
		ImageView imagePerfil;
	}
}
