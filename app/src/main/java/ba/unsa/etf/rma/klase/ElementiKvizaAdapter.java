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

    private int resource = R.layout.row_view;

    public ElementiKvizaAdapter(Context context, int resource, ArrayList<Pitanje> pitanja) {
        super(context, 0, pitanja);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        Pitanje currPitanje = getItem(position);
        TextView nazivPitanja = convertView.findViewById(R.id.kviz);
        ImageView iwKategorija = convertView.findViewById(R.id.iwKategorija);

        if (currPitanje != null) {
            nazivPitanja.setText(currPitanje.getNaziv());
            if (currPitanje.getNaziv().equals("Dodaj pitanje")) {
                iwKategorija.setImageResource(R.drawable.plus);
            } else {
                iwKategorija.setImageResource(R.drawable.circle);
            }
        }

        return convertView;
    }
}

