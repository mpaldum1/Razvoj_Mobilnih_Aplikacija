package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.KategorijaAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizAdapter;

public class KvizoviAkt extends AppCompatActivity {

    private Spinner spPostojeceKategorije;
    private ListView lwkvizovi;

    private ArrayList<Kviz> listaKvizova;
    private ArrayList<Kategorija> listaKategorija;
    private KvizAdapter adapterKviz;
    private KategorijaAdapter adapterKategorija;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);

        spPostojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);
        lwkvizovi = (ListView) findViewById(R.id.lvKvizovi);

        initSpinner();
        initList();

        adapterKviz = new KvizAdapter(this, R.layout.row_view, listaKvizova);
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);

        lwkvizovi.setAdapter(adapterKviz);
        spPostojeceKategorije.setAdapter(adapterKategorija);

        spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija clickedKategorija = (Kategorija) parent.getItemAtPosition(position);
                String clickedKategorijaNaziv = clickedKategorija.getNaziv();

                Toast.makeText(KvizoviAkt.this, clickedKategorijaNaziv, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lwkvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (KvizoviAkt.this, DodajKvizAkt.class);
                startActivity(intent);
            }
        });
    }

    public void dodajKviz (Kviz kviz){
        if(kviz != null) {
            listaKvizova.add(kviz);
        }
    }

    private void initSpinner(){
        listaKategorija = new ArrayList<>();
        listaKategorija.add(new Kategorija("Kategorija", "-1"));
    }

    private void initList(){
        listaKvizova = new ArrayList<>();
        final Kviz dodajKviz = new Kviz("Dodaj kviz", null, new Kategorija("", Integer.toString(R.drawable.plus)));
        listaKvizova.add(dodajKviz);
    }
}
