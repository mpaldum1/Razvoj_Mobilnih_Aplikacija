package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.servisi.InsertUBazu;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.token;

public class DodajPitanjeAkt extends AppCompatActivity {

    private ListView lvOdgovori;
    private EditText etNaziv, etOdgovor;
    private Button btnDodajOdgovor, btnDodajTacan, btnDodajPitanje;

    private ArrayAdapter<String> adapterOdgovori;

    private ArrayList<String> listaOdgovora = new ArrayList<>();
    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private String odgovor = "";

    private Pitanje povratnoPitanje;
    private String nazivPitanja = "";
    private String tacanOdgovor;


    boolean flag = false;                       // da li je u pitanju tacan odgovor
    boolean tacanIma = false;                   // da li i dalje u listi imamo tacan odgovor


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        init();                                 // inicijalizacija

        Intent povratniIntent = getIntent();
        listaPitanja = povratniIntent.getParcelableArrayListExtra("Lista pitanja");
        lvOdgovori.setAdapter(adapterOdgovori);


        btnDodajOdgovor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                if (validationCkeckDodaj()) {
                    listaOdgovora.add(etOdgovor.getText().toString());
                    adapterOdgovori.notifyDataSetChanged();
                    etOdgovor.setText("");
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
                    etOdgovor.setText("");
                }
            }
        });

        btnDodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                if (validationCkeck() && !tacanOdgovor.equals("")) {

                    povratnoPitanje = new Pitanje(nazivPitanja, nazivPitanja, tacanOdgovor, listaOdgovora);
                    listaPitanja.add(povratnoPitanje);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Povratno pitanje", povratnoPitanje);
                    returnIntent.putExtra("Odgovori", listaOdgovora);

                    setResult(RESULT_OK, returnIntent);
                    InsertUBazu insertUBazu = new InsertUBazu();
                    insertUBazu.setToken(token);
                    insertUBazu.setMethod("POST");
                    insertUBazu.setNazivKolekcije("Pitanja");
                    insertUBazu.setPitanje(povratnoPitanje);
                    insertUBazu.setOdgovori(listaOdgovora);
                    insertUBazu.execute();

                    finish();
                }
            }
        });

        lvOdgovori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!listaOdgovora.isEmpty()) {
                    String clickedPitanje = (String) parent.getItemAtPosition(position);

                    if (clickedPitanje.equals(tacanOdgovor)) {
                        tacanIma = false;
                        tacanOdgovor = "";
                    }

                    listaOdgovora.remove(lvOdgovori.getItemAtPosition(position));
                    adapterOdgovori.notifyDataSetChanged();
                }
            }
        });

    }

    private void init() {

        // bindamo sve elemente
        lvOdgovori = findViewById(R.id.lvOdgovori);
        etNaziv = findViewById(R.id.etNaziv);
        etOdgovor = findViewById(R.id.etOdgovor);
        btnDodajOdgovor = findViewById(R.id.btnDodajOdgovor);
        btnDodajTacan = findViewById(R.id.btnDodajTacan);
        btnDodajPitanje = findViewById(R.id.btnDodajPitanje);

        // adapter liste odgovora
        adapterOdgovori = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaOdgovora) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                if (flag && tacanIma && listaOdgovora.get(position).equals(tacanOdgovor)) {                     // imamo li tacan odgovor
                    text.setTextColor(getResources().getColor(R.color.tacanOdgovor));
                } else {
                    text.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
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


        for (Pitanje trenutni : listaPitanja) {
            if (trenutni.getNaziv().equals(nazivPitanja)) {
                correct = false;
                this.getWindow().getDecorView().findViewById(R.id.etNaziv).setBackgroundResource(R.color.colorRedValidation);
                Toast.makeText(this, "Unesite jedinstveni naziv pitanja kviza", Toast.LENGTH_SHORT).show();
                // ime mora biti jedinstveno
                break;
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
