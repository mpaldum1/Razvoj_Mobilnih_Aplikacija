package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class KategorijaAdapter extends ArrayAdapter<Kategorija> {
    public KategorijaAdapter(Context context, ArrayList<Kategorija> kategorije) {
        super(context,0, kategorije);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_layout,parent,false);
        }
        TextView nazivKategorije = convertView.findViewById(R.id.twSpinner);
        Kategorija currentKategorija = getItem(position);

        if(currentKategorija != null){
            nazivKategorije.setText(currentKategorija.getNaziv());
        }

        return  convertView;
    }

}
