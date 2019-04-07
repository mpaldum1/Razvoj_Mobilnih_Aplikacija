package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajPitanjeAkt extends AppCompatActivity {

    private ListView lvOdgovori;
    private EditText etNaziv, etOdgovor;
    private Button btnDodajOdgovor, btnDodajTacan, btnDodajPitanje;

    private ArrayAdapter<String> adapterOdgovori;
    private ArrayList<Kviz> listaKvizova;

    private ArrayList<String> listaOdgovora;
    private String odgovor = "";

    private Pitanje povratnoPitanje;
    private String nazivPitanja = "";
    private String tacanOdgovor;


    boolean flag = false;
    boolean tacanIma = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        init();
        Intent povratniIntent = getIntent();
        listaKvizova = povratniIntent.getParcelableArrayListExtra("Kvizovi");


        btnDodajOdgovor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                if (validationCkeckDodaj()) {
                    listaOdgovora.add(etOdgovor.getText().toString());
                    adapterOdgovori.notifyDataSetChanged();
                }
            }
        });

        btnDodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                if (validationCkeckDodaj() && validationCkeckTacan()) {
                    tacanOdgovor = etOdgovor.getText().toString();
                    listaOdgovora.add(tacanOdgovor);
                    flag = true;
                    tacanIma = true;
                    adapterOdgovori.notifyDataSetChanged();
                }
            }
        });

        btnDodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                if (validationCkeck()) {

                    povratnoPitanje = new Pitanje(nazivPitanja, nazivPitanja, odgovor, listaOdgovora);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Povratno pitanje", povratnoPitanje);

                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }


    private void init() {

        lvOdgovori = findViewById(R.id.lvOdgovori);
        etNaziv = findViewById(R.id.etNaziv);
        etOdgovor = findViewById(R.id.etOdgovor);
        btnDodajOdgovor = findViewById(R.id.btnDodajOdgovor);
        btnDodajTacan = findViewById(R.id.btnDodajTacan);
        btnDodajPitanje = findViewById(R.id.btnDodajPitanje);

        listaOdgovora = new ArrayList<>();
        adapterOdgovori = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaOdgovora) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                if (flag) {
                    text.setTextColor(getResources().getColor(R.color.tacanOdgovor));
                    flag = false;
                }
                return view;
            }
        };

        lvOdgovori.setAdapter(adapterOdgovori);

        listaKvizova = new ArrayList<>();

    }

    private boolean validationCkeck() {

        boolean correct = true;
        nazivPitanja = etNaziv.getText().toString();

        if (nazivPitanja.equals("")) {
            this.getWindow().getDecorView().findViewById(R.id.etNaziv).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }

        if (listaOdgovora == null || listaOdgovora.isEmpty()) {
            // moramo imati bar jedan odgovor
            this.getWindow().getDecorView().findViewById(R.id.lvOdgovori).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }


        if (listaKvizova != null) {
            for (Kviz trenutni : listaKvizova) {
                if (trenutni.getNaziv().equals(nazivPitanja)) {
                    correct = false;
                    this.getWindow().getDecorView().findViewById(R.id.etNaziv).setBackgroundResource(R.color.colorRedValidation);
                    // ime mora biti jedinstveno
                    break;
                }
            }
        }
        return correct;
    }

    private boolean validationCkeckDodaj() {

        boolean correct = true;
        if (etOdgovor.getText() != null) {
            odgovor = etOdgovor.getText().toString();
        } else correct = false;

        if (odgovor.equals("")) correct = false;

        if (listaOdgovora != null) {
            for (String trenutni : listaOdgovora) {
                if (trenutni.equals(odgovor)) {
                    correct = false;
                    break;
                }
            }
        }
        if (!correct) {
            this.getWindow().getDecorView().findViewById(R.id.etOdgovor).setBackgroundResource(R.color.colorRedValidation);
        }

        return correct;
    }

    private boolean validationCkeckTacan() {

        boolean correct = true;
        odgovor = etOdgovor.getText().toString();


        if (odgovor.equals("") || tacanIma) {
            // moramo imati tacan odgovor i zabranjujemo dodavanje novog
            this.getWindow().getDecorView().findViewById(R.id.etOdgovor).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }
        return correct;
    }

    private void clear() {
        etNaziv.setBackgroundResource(android.R.drawable.edit_text);
        etOdgovor.setBackgroundResource(android.R.drawable.edit_text);
        lvOdgovori.setBackgroundResource(android.R.drawable.list_selector_background);
    }
}
