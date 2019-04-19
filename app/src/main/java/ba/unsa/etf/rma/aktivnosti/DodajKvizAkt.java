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
import ba.unsa.etf.rma.adapteri.ElementiKvizaAdapter;
import ba.unsa.etf.rma.adapteri.KategorijaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {

    private Spinner spKategorije;
    private ListView lvDodanaPitanja;
    private ListView lvMogucaPitanja;
    private EditText etNaziv;
    private Button btnDodajKviz;

    private KategorijaAdapter adapterKategorija;
    private ElementiKvizaAdapter adapterPitanja;
    private ElementiKvizaAdapter adapterMogucaPitanja;

    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    private ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private ArrayList<Pitanje> listaMogucihPitanja = new ArrayList<>();

    private Kviz trenutniKviz;
    private Kategorija trenutnaKategorija;

    public DodajKvizAkt() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        initialize();

        listaKategorija.removeIf(kategorija -> kategorija.getId().equals("-2"));                 //pritisnuto neko pitanje
        listaKategorija.add(new Kategorija("Dodaj kategoriju", "-2"));

        int positionKategorija = 0;
        if (!trenutniKviz.getNaziv().equals("Dodaj kviz")) {                                          // pritisnuto dodaj kviz
            etNaziv.setText(trenutniKviz.getNaziv());


            for(Kviz trenutni: listaKvizova){
                if(trenutni.getNaziv().equals(trenutniKviz.getNaziv())) {
                    trenutni.setNaziv("");
                }
            }

            for(Kategorija trenutna :listaKategorija){
                if(trenutna.getId().equals(trenutnaKategorija.getId())){                              // postavljamo spinner na kategoriju kviza
                    break;
                }
                positionKategorija++;
            }

        } else {

            if (listaPitanja == null) {
                listaPitanja = new ArrayList<>();
                listaPitanja.add(new Pitanje("Dodaj pitanje", "", "", null));
            }
        }

        adapterPitanja = new ElementiKvizaAdapter(this, R.layout.row_view, listaPitanja);       //postavljamo adaptere
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);
        adapterMogucaPitanja = new ElementiKvizaAdapter(this, R.layout.row_view, listaMogucihPitanja);

        lvDodanaPitanja.setAdapter(adapterPitanja);
        spKategorije.setAdapter(adapterKategorija);
        lvMogucaPitanja.setAdapter(adapterMogucaPitanja);

        spKategorije.setSelection(positionKategorija);

        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {                 // spinner listener
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trenutnaKategorija = (Kategorija) parent.getItemAtPosition(position);

                if (trenutnaKategorija.getId().equals("-1")) {
                    // empty
                } else {
                    Toast.makeText(DodajKvizAkt.this, trenutniKviz.getNaziv(), Toast.LENGTH_SHORT).show();

                    if (trenutnaKategorija.getId().equals("-2")) {                                            // Pritisnuto "Dodaj Kategoriju"

                        Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                        intent.putExtra("Pressed kategorije", listaKategorija.get(position));
                        startActivityForResult(intent, 2);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });


        lvDodanaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {                           // listView listener pitanja u kvizu
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pitanje temp = (Pitanje) parent.getItemAtPosition(position);

                if (temp.getNaziv().equals("Dodaj pitanje")) {

                    Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    intent.putExtra("Lista pitanja", listaPitanja);
                    startActivityForResult(intent, 3);

                } else {
                    listaPitanja.remove(adapterPitanja.getItem(position));
                    adapterPitanja.notifyDataSetChanged();
                    listaMogucihPitanja.add(temp);
                    adapterMogucaPitanja.notifyDataSetChanged();
                }

            }
        });

        lvMogucaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {              //list view Moguca pitanja
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pitanje temp = (Pitanje) parent.getItemAtPosition(position);
                listaMogucihPitanja.remove(adapterMogucaPitanja.getItem(position));
                adapterMogucaPitanja.notifyDataSetChanged();

                Pitanje dodaj = listaPitanja.get(listaPitanja.size() - 1);
                listaPitanja.remove(listaPitanja.size() - 1);
                listaPitanja.add(temp);
                listaPitanja.add(dodaj);
                adapterPitanja.notifyDataSetChanged();
            }
        });

        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validationCkeckNaziv() && validationCheckKategorija()) {

                    //sve ok
                    clear();
                    trenutniKviz.setNaziv(etNaziv.getText().toString());
                    trenutniKviz.setPitanja(listaPitanja);
                    trenutniKviz.setKategorija(trenutnaKategorija);

                    adapterPitanja.notifyDataSetChanged();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Povratni kviz", trenutniKviz);
                    returnIntent.putExtra("Povratna kategorija", trenutnaKategorija);
                    returnIntent.putExtra("Povratne kategorije", listaKategorija);
                    returnIntent.putExtra("Povratna pitanja", listaPitanja);

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

        if (requestCode == 3) {                                 // povratak iz dodaj pitanje aktivnost
            if (resultCode == RESULT_OK) {
                povratnoPitanje = data.getParcelableExtra("Povratno pitanje");
                Pitanje zamjena = listaPitanja.get(listaPitanja.size() - 1);

                listaPitanja.set(listaPitanja.size() - 1, povratnoPitanje);
                listaPitanja.add(zamjena);

                adapterPitanja.notifyDataSetChanged();
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {                      //povratak iz dodaj kategoriju aktivnost

                povratnaKategorija = data.getParcelableExtra("Povratna kategorija");

                if(povratnaKategorija != null) {
                    Kategorija zamjena = listaKategorija.get(listaKategorija.size() - 1);            //'Svi ostaje na kraju
                    listaKategorija.remove(listaKategorija.size() - 1);

                    trenutnaKategorija = povratnaKategorija;
                    listaKategorija.add(povratnaKategorija);
                    listaKategorija.add(zamjena);

                    adapterKategorija.notifyDataSetChanged();
                    spKategorije.setSelection(listaKategorija.indexOf(trenutnaKategorija));
                }

            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("Povratne kategorije", listaKategorija);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void initialize() {

        spKategorije = findViewById(R.id.spKategorije);
        lvDodanaPitanja = findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = findViewById(R.id.lvMogucaPitanja);
        etNaziv = findViewById(R.id.etNaziv);
        btnDodajKviz = findViewById(R.id.btnDodajKviz);

        listaMogucihPitanja = new ArrayList<>();

        Intent intent = getIntent();
        trenutniKviz = intent.getParcelableExtra("Pressed kviz");
        listaKategorija = intent.getParcelableArrayListExtra("Moguce kategorije");
        listaKvizova = intent.getParcelableArrayListExtra("Kvizovi");
        trenutnaKategorija = intent.getParcelableExtra("Trenutna kategorija");
        listaPitanja = intent.getParcelableArrayListExtra("Pitanja kviza");

    }

    private boolean validationCkeckNaziv() {

        String naziv = etNaziv.getText().toString();
        boolean correct = true;

        if (naziv.equals("")) {

            Toast.makeText(this, "Unesite naziv kviza!", Toast.LENGTH_SHORT).show();
            correct = false;
        }

        for (Kviz trenutni : listaKvizova) {                                                  // ne smijemo imati naziv postojeceg kviza
            if (trenutni.getNaziv().equals(naziv)) {
                Toast.makeText(this, "Ime kviza mora biti jedinstveno!", Toast.LENGTH_SHORT).show();
                correct = false;
            }
        }

        if (!correct) {
            this.getWindow().getDecorView().findViewById(R.id.etNaziv).setBackgroundResource(R.color.colorRedValidation);
        }
        return correct;
    }

    private boolean validationCheckKategorija() {
        etNaziv.setBackgroundResource(android.R.drawable.edit_text);
        boolean correct = true;
        if (trenutnaKategorija == null || Integer.parseInt(trenutnaKategorija.getId()) < 0) {
            Toast.makeText(this, "Unesite kategoriju kviza", Toast.LENGTH_SHORT).show();
            this.getWindow().getDecorView().findViewById(R.id.spKategorije).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }

        return correct;
    }

    private void clear() {
        spKategorije.setBackgroundResource(android.R.drawable.spinner_background);
    }

}
