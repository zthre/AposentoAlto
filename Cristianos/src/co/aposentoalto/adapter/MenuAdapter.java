package co.aposentoalto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.aposentoalto.R;

public class MenuAdapter extends BaseAdapter {

	String[] array;
	private Context ctx;

	public MenuAdapter(Context c, String[] array) {
		this.array = array;
		this.ctx = c;
	}

	@Override
	public int getCount() {
		return array.length;
	}

	@Override
	public Object getItem(int arg0) {

		return arg0;
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;
		if (row == null) { // if it's not recycled, initialize some
							// attributes
			LayoutInflater inflater = LayoutInflater.from(ctx);
			row = inflater.inflate(R.layout.fragment_inicio_menu, null);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.menu_text);
			holder.txtDecrip = (TextView) row.findViewById(R.id.menu_text2);
			holder.imageItem = (ImageView) row.findViewById(R.id.menu_icono);
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		
		Bitmap icono = null;
			int idDrawable = ctx.getResources()
					.getIdentifier("ic_"+array[position].toLowerCase(),
						"drawable", ctx.getPackageName());
		icono = BitmapFactory.decodeResource(ctx.getResources(), idDrawable);

		holder.txtTitle.setText(array[position]);
		holder.txtDecrip.setText("");
		holder.imageItem.setImageBitmap(icono);
		return row;
	}

	static class RecordHolder {
		TextView txtTitle;
		TextView txtDecrip;
		ImageView imageItem;
	}

}