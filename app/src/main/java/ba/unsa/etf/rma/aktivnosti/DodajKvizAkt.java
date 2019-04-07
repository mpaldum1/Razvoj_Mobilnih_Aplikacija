package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.ElementiKvizaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.KategorijaAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {

    private Spinner spKategorije;
    private ListView lvDodanaPitanja;
    private ListView lvMogucaPitanja;
    private EditText etNaziv;
    private Button btnDodajKviz;

    private KategorijaAdapter adapterKategorija;
    private ElementiKvizaAdapter adapterElementiKviza;

    private ArrayList<Pitanje> listaPitanja;
    private ArrayList<Kategorija> listaKategorija;
    private Kviz trenutniKviz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        Intent intent = getIntent();
        trenutniKviz = intent.getParcelableExtra("Pressed kviz");
        listaKategorija = intent.getParcelableArrayListExtra("Moguce kategorije");

        init();

        listaPitanja = trenutniKviz.getPitanja();
        lvDodanaPitanja.setAdapter(adapterElementiKviza);
        spKategorije.setAdapter(adapterKategorija);


        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {          // spinner listener
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija clickedKategorija = (Kategorija) parent.getItemAtPosition(position);
                String clickedKategorijaNaziv = clickedKategorija.getNaziv();

                if (clickedKategorija.getId().equals("-1")) {
                    // empty
                } else {
                    Toast.makeText(DodajKvizAkt.this, clickedKategorijaNaziv, Toast.LENGTH_SHORT).show();

                    if (clickedKategorija.getId().equals("0")) {                                            // Pritisnuto "Dodaj Kategoriju"
                        // pozivamo aktivnost dodajKategoriju
                    }
                    else {

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init(){                                                                // pripremamo pocetno stanje

        spKategorije = (Spinner)findViewById(R.id.spKategorije);
        lvDodanaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        btnDodajKviz = (Button) findViewById(R.id.btnDodajKviz);

        listaPitanja = new ArrayList<>();

       // listaKategorija.removeIf( kategorija -> kategorija.getId().equals("0"));
        listaKategorija.add(new Kategorija("Dodaj kategoriju", "0"));

        adapterElementiKviza = new ElementiKvizaAdapter(this,R.layout.row_view, listaPitanja);
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);

    }

}
