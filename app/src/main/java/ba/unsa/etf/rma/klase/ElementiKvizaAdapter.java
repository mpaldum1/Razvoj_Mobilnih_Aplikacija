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

public class ElementiKvizaAdapter extends ArrayAdapter<Pitanje> {

    private Context context;
    private int resource = R.layout.row_view;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();

    public ElementiKvizaAdapter (Context context, int resource, ArrayList<Pitanje> pitanja){
        super(context, resource, pitanja);
        this.resource= resource;
        this.context = context;
        this.pitanja = pitanja;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Pitanje pitanje = pitanja.get(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView twPitanje = (TextView) convertView.findViewById(R.id.kviz);
        ImageView iwKategorija = (ImageView) convertView.findViewById(R.id.iwKategorija);

        twPitanje.setText(pitanje.getNaziv());
        if(pitanje.getNaziv().equals("Dodaj pitanje")){
            iwKategorija.setImageResource(R.drawable.plus);
        }
        else {
            iwKategorija.setImageResource(R.drawable.circle);
        }

        return  convertView;
    }
}

