package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
    private ArrayList<Kviz> listaKvizova;
    ArrayList<Pitanje> listaSvihPitanja = new ArrayList<>();
    private Kviz trenutniKviz, temporalKviz;
    private Kategorija trenutnaKategorija;

    private Pitanje dodajPitanje;

    public DodajKvizAkt() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        Intent intent = getIntent();
        trenutniKviz = intent.getParcelableExtra("Pressed kviz");
        listaKategorija = intent.getParcelableArrayListExtra("Moguce kategorije");
        listaKvizova = intent.getParcelableArrayListExtra("Kvizovi");

        spKategorije = (Spinner) findViewById(R.id.spKategorije);
        lvDodanaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        btnDodajKviz = (Button) findViewById(R.id.btnDodajKviz);

        if (!trenutniKviz.getNaziv().equals("Dodaj kviz")) {
            etNaziv.setText(trenutniKviz.getNaziv());
            listaPitanja = trenutniKviz.getPitanja();

        } else {
            init();
        }

        adapterElementiKviza = new ElementiKvizaAdapter(this, R.layout.row_view, listaPitanja);
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);


        lvDodanaPitanja.setAdapter(adapterElementiKviza);
        spKategorije.setAdapter(adapterKategorija);

        if (trenutniKviz != null) {
            listaPitanja = trenutniKviz.getPitanja();
            listaPitanja.add(dodajPitanje);
        }


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

                        Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                        intent.putExtra("Pressed kategorije", listaKategorija.get(position));
                        startActivityForResult(intent, 2);

                    } else {
                        String naziv = etNaziv.getText().toString();
                        adapterKategorija.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });


        lvDodanaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {                           // listView listener
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pitanje temp = (Pitanje) parent.getItemAtPosition(position);

                if (temp != null && temp.getNaziv().equals("Dodaj pitanje")) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    intent.putExtra("Kvizovi", listaKvizova);
                    intent.putExtra("Pressed kviz", listaPitanja.get(position));
                    startActivityForResult(intent, 3);
                }
            }
        });

        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                if (validationCkeck()) {
                    trenutniKviz.setNaziv(etNaziv.getText().toString());
                    trenutniKviz.setPitanja(listaPitanja);

                    adapterElementiKviza.notifyDataSetChanged();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Povratni kviz", trenutniKviz);
                    returnIntent.putExtra("Povratna kategorija", trenutnaKategorija);

                    setResult(RESULT_OK, returnIntent);
                    finish();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Pitanje povratnoPitanje;
        Kategorija povratnaKategorija;

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                povratnoPitanje = data.getParcelableExtra("Povratno pitanje");
                Pitanje zamjena = listaPitanja.get(listaPitanja.size() - 1);

                listaPitanja.set(listaPitanja.size() - 1, povratnoPitanje);
                listaPitanja.add(zamjena);

                adapterElementiKviza.notifyDataSetChanged();
                ElementiKvizaAdapter novi = new ElementiKvizaAdapter(this, R.layout.row_view, listaPitanja);
                lvDodanaPitanja.setAdapter(novi);
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {

                povratnaKategorija = data.getParcelableExtra("Povratna kategorija");

                trenutnaKategorija = povratnaKategorija;
                trenutnaKategorija.setId(Integer.toString(R.drawable.circle));
                listaKategorija.add(povratnaKategorija);
                adapterKategorija.notifyDataSetChanged();

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {                                                                // pripremamo pocetno stanje

        listaKategorija.removeIf(kategorija -> kategorija.getId().equals("0"));
        listaKategorija.add(new Kategorija("Dodaj kategoriju", "0"));

        if (listaPitanja == null) {
            listaPitanja = new ArrayList<>();
            dodajPitanje = new Pitanje("Dodaj pitanje", "", "", null);
            listaPitanja.add(dodajPitanje);
        }

    }

    private boolean validationCkeck() {

        boolean correct = true;
        String naziv = etNaziv.getText().toString();

        if (naziv.equals("")) {
            this.getWindow().getDecorView().findViewById(R.id.etNaziv).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }

        return correct;
    }

    private void clear() {
        etNaziv.setBackgroundResource(android.R.drawable.edit_text);
    }

}
