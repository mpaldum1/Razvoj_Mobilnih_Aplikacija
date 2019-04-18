package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconHelper;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class KvizAdapter extends ArrayAdapter<Kviz> {

    private Context context;
    private int resource = R.layout.row_view;
    private ArrayList<Kviz> kvizovi = new ArrayList<>();

    public KvizAdapter(Context context, int resource, ArrayList<Kviz> kvizovi) {
        super(context, resource, kvizovi);
        this.resource = resource;
        this.context = context;
        this.kvizovi = kvizovi;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Kviz kviz = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView twKviz = (TextView) convertView.findViewById(R.id.kviz);
        ImageView iwKategorija = (ImageView) convertView.findViewById(R.id.iwKategorija);

        IconHelper helper = IconHelper.getInstance(context);
        Icon icon = helper.getIcon(Integer.parseInt(kviz.getKategorija().getId()));

        helper.addLoadCallback(new IconHelper.LoadCallback() {
            @Override
            public void onDataLoaded() {
                if(icon != null)
                iwKategorija.setImageDrawable(icon.getDrawable(context));
            }
        });
        twKviz.setText(kviz.getNaziv());

        if(kviz.getNaziv().equals("Dodaj kviz")){
            iwKategorija.setImageResource(R.drawable.plus);
        }
        else if(Integer.parseInt(kviz.getKategorija().getId()) < 0) {
            iwKategorija.setImageResource(R.drawable.circle);
        }

        return convertView;
    }
}
