package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class KvizAdapter extends ArrayAdapter<Kviz> {

    private Context context;
    private int resource = R.layout.row_view;
    private ArrayList<Kviz> kvizovi = new ArrayList<>();

    public KvizAdapter(Context context, int resource, ArrayList<Kviz> kvizovi){
        super(context, resource, kvizovi);
        this.resource= resource;
        this.context = context;
        this.kvizovi = kvizovi;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    Kviz kviz = kvizovi.get(position);

    if(convertView == null){
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);
    }

    TextView twKviz = (TextView) convertView.findViewById(R.id.kviz);
    ImageView iwKategorija = (ImageView) convertView.findViewById(R.id.iwKategorija);

    twKviz.setText(kviz.getNaziv());
    iwKategorija.setImageResource(Integer.parseInt(kviz.getKategorija().getId()));

        return  convertView;
    }
}
