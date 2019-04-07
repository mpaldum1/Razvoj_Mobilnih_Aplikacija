package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private ArrayList<Kviz> filterListKvizova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);

        spPostojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);
        lwkvizovi = (ListView) findViewById(R.id.lvKvizovi);

        listaKvizova = new ArrayList<>();
        filterListKvizova = new ArrayList<>();

        initSpinner();
        initList(listaKvizova);
        initList(filterListKvizova);

        adapterKviz = new KvizAdapter(this, R.layout.row_view, filterListKvizova);
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);

        lwkvizovi.setAdapter(adapterKviz);
        spPostojeceKategorije.setAdapter(adapterKategorija);


        spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {          // spinner listener
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija clickedKategorija = (Kategorija) parent.getItemAtPosition(position);
                String clickedKategorijaNaziv = clickedKategorija.getNaziv();

                if (clickedKategorija.getId().equals("-1")) {
                    // empty
                } else {
                    Toast.makeText(KvizoviAkt.this, clickedKategorijaNaziv, Toast.LENGTH_SHORT).show();

                    if (clickedKategorija.getId().equals("0")) {                                            // Pritisnuto "Sve"
                        filterListKvizova = listaKvizova;
                    } else {
                        filterListKvizova.clear();
                        initList(filterListKvizova);

                        // Filtriramo

                        for (Kviz currentKviz : listaKvizova) {
                            if (currentKviz.getKategorija().getId().equals((clickedKategorija.getId()))) {
                                filterListKvizova.add(currentKviz);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lwkvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {                           // listView listener
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                intent.putExtra("Pressed kviz", filterListKvizova.get(position));
                intent.putExtra("Moguce kategorije", listaKategorija);
                intent.putExtra("Kvizovi", listaKvizova);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Kviz povratniKviz = data.getParcelableExtra("Povratni kviz");
                Kategorija povratnaKategorija = data.getParcelableExtra("Povratna kategorija");

                if (povratniKviz != null && povratnaKategorija != null) {

                    povratniKviz.setKategorija(povratnaKategorija);
                    Kviz zamjena = listaKvizova.get(listaKvizova.size() - 1);

                    listaKvizova.set(listaKvizova.size() - 1, povratniKviz);
                    filterListKvizova.set(filterListKvizova.size() - 1, povratniKviz);

                    listaKvizova.add(zamjena);
                    filterListKvizova.add(zamjena);
                    adapterKviz.notifyDataSetChanged();
                    adapterKategorija.notifyDataSetChanged();

                    KvizAdapter novi = new KvizAdapter(this, R.layout.row_view, filterListKvizova);
                    lwkvizovi.setAdapter(novi);
                    KategorijaAdapter noviADK = new KategorijaAdapter(this, listaKategorija);
                    spPostojeceKategorije.setAdapter(noviADK);

                }
            }
        }
    }


    public void dodajKviz(Kviz kviz) {
        if (kviz != null) {
            listaKvizova.add(kviz);
        }
    }

    private void initSpinner() {
        listaKategorija = new ArrayList<>();
        listaKategorija.add(new Kategorija("Kategorije", "-1"));
        listaKategorija.add(new Kategorija("Svi", "0"));
    }

    private void initList(ArrayList<Kviz> listaKvizova) {
        final Kviz dodajKviz = new Kviz("Dodaj kviz", null, new Kategorija("", Integer.toString(R.drawable.plus)));
        listaKvizova.add(dodajKviz);
    }
}
