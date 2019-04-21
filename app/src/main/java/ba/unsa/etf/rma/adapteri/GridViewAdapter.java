package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconHelper;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class GridViewAdapter extends BaseAdapter {
    private  ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private  Context context;
    private  int resource = R.layout.grid_view_element;

    public GridViewAdapter(ArrayList<Kviz> listaKvizova, Context mContext) {
        this.listaKvizova = listaKvizova;
        this.context = mContext;
    }

    @Override
    public int getCount() {
        return listaKvizova.size();
    }

    @Override
    public Object getItem(int position) {
        return listaKvizova.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Kviz trenutniKviz = (Kviz) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvNazivKviza = (TextView) convertView.findViewById(R.id.gridNazivKviza);
        TextView tvUkupnoPitanja = (TextView) convertView.findViewById(R.id.gridUkupnoPitanja);
        ImageView ivKategorija = (ImageView) convertView.findViewById(R.id.gridImageView);


        IconHelper helper = IconHelper.getInstance(context);
        Icon icon = helper.getIcon(Integer.parseInt(trenutniKviz.getKategorija().getId()));

        helper.addLoadCallback(new IconHelper.LoadCallback() {
            @Override
            public void onDataLoaded() {
                if(icon != null)
                    ivKategorija.setImageDrawable(icon.getDrawable(context));
            }
        });

        tvNazivKviza.setText(trenutniKviz.getNaziv());
        tvUkupnoPitanja.setText(Integer.toString(trenutniKviz.getPitanja().size()));
        if(Integer.parseInt(trenutniKviz.getKategorija().getId()) < 0) {
            ivKategorija.setImageResource(R.drawable.circle);
        }

        return convertView;
    }
}
