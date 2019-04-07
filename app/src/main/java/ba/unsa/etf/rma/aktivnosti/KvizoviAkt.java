package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizAdapter;

public class KvizoviAkt extends AppCompatActivity {

    private Spinner postojeceKategorije;
    private ListView lwkvizovi;
    private ArrayList<Kviz> listaKvizova;
    private KvizAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);


        postojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);
        lwkvizovi = (ListView) findViewById(R.id.lvKvizovi);
        listaKvizova = new ArrayList<>();

        final Kviz dodajKviz = new Kviz("Dodaj kviz", null, new Kategorija("", Integer.toString(R.drawable.plus)));
        listaKvizova.add(dodajKviz);

        adapter = new KvizAdapter(this, R.layout.row_view, listaKvizova);
        lwkvizovi.setAdapter(adapter);

        lwkvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (KvizoviAkt.this, DodajKvizAkt.class);
                startActivity(intent);
            }
        });
    }

    void dodajKviz (Kviz kviz){
        if(kviz != null) {
            listaKvizova.add(kviz);
        }
    }
}
