package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ba.unsa.etf.rma.R;

public class PitanjeFragAdapter extends ArrayAdapter<String> {

private int resource = R.layout.jednostavni_red;
private String tacniOdgovor;
private TextView red;
private String odgovor;

    public PitanjeFragAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        odgovor = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);

        }
        TextView red = convertView.findViewById(R.id.tvJenostavniRed);

        return convertView;
    }

     public void setTacni(String tacniOdgovor) {
        this.tacniOdgovor = tacniOdgovor;
     }
     public void obojiPritisnuti() {
        if(!odgovor.equals(tacniOdgovor)) {
            red.setBackgroundResource(R.color.crvena);
        }

     }
     public void obojiTacni(){
             red.setBackgroundResource(R.color.zelena);
     }
}
