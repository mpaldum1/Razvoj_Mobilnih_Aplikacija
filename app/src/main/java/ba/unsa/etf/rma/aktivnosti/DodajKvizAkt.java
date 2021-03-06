package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ElementiKvizaAdapter;
import ba.unsa.etf.rma.adapteri.KategorijaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.servisi.FetchPitanjaBaza;
import ba.unsa.etf.rma.servisi.InsertUBazu;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.moguDodatiKategoriju;
import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.moguDodatiPitanje;
import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.token;

public class DodajKvizAkt extends AppCompatActivity {

    private Context context;

    private Spinner spKategorije;
    private ListView lvDodanaPitanja;
    private ListView lvMogucaPitanja;
    private EditText etNaziv;
    private Button btnDodajKviz;
    private Button btnImportKviz;

    private KategorijaAdapter adapterKategorija;
    private ElementiKvizaAdapter adapterPitanja;
    private ElementiKvizaAdapter adapterMogucaPitanja;

    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    private ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private ArrayList<Pitanje> listaMogucihPitanja = new ArrayList<>();

    private Kviz trenutniKviz;
    private Kategorija trenutnaKategorija;
    private Kategorija dodajKategoriju;
    private FetchPitanjaBaza fetchPitanjaBaza;

    private ArrayList<String> povratniOdgovori = new ArrayList<>();

    private Boolean isPatch = false;


    private static final int READ_REQUEST_CODE = 42;

    public DodajKvizAkt() {
    }

    public void setLvMogucaPitanja(ListView lvMogucaPitanja) {
        this.lvMogucaPitanja = lvMogucaPitanja;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);
        context = this;

        initialize();
        //pritisnuto neko pitanje


        adapterPitanja = new ElementiKvizaAdapter(this, R.layout.row_view, listaPitanja);       //postavljamo adaptere
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);
        adapterMogucaPitanja = new ElementiKvizaAdapter(this, R.layout.row_view, listaMogucihPitanja);

        lvDodanaPitanja.setAdapter(adapterPitanja);
        spKategorije.setAdapter(adapterKategorija);
        lvMogucaPitanja.setAdapter(adapterMogucaPitanja);


        dodajKategoriju = new Kategorija("Dodaj kategoriju", "-3");
        if (!listaKategorija.contains(dodajKategoriju)) {
            listaKategorija.add(dodajKategoriju);
        }

        int positionKategorija = 0;
        if (!trenutniKviz.getNaziv().equals("Dodaj kviz")) {                                          // pritisnuto dodaj kviz
            etNaziv.setText(trenutniKviz.getNaziv());


            for (Kviz trenutni : listaKvizova) {
                if (trenutni.getNaziv().equals(trenutniKviz.getNaziv())) {
                    trenutni.setNaziv("");
                }
            }

            for (Kategorija trenutna : listaKategorija) {
                if (trenutna.getId().equals(trenutnaKategorija.getId())) {                              // postavljamo spinner na kategoriju kviza
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
        spKategorije.setSelection(positionKategorija);

        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {                 // spinner listener
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trenutnaKategorija = (Kategorija) parent.getItemAtPosition(position);
                connectionCheck();

                if (trenutnaKategorija.getId().equals("-1")) {
                    // empty
                } else {
                    if (trenutnaKategorija.getId().equals("-3")) {                                            // Pritisnuto "Dodaj Kategoriju"

                        if (moguDodatiKategoriju) {
                            Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                            intent.putExtra("Pressed kategorije", listaKategorija);
                            startActivityForResult(intent, 2);
                        } else {
                            DodajKvizAkt.dialogIspis("Zabranjeno dodavanje kategorija u offline režimu!", getContext());
                            spKategorije.setSelection(0);
                        }
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
                connectionCheck();

                if (temp.getNaziv().equals("Dodaj pitanje")) {

                    if (moguDodatiPitanje) {
                        Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                        intent.putExtra("Lista pitanja", listaPitanja);
                        startActivityForResult(intent, 3);
                    } else {
                        DodajKvizAkt.dialogIspis("Zabranjeno dodavanje pitanja u offline režimu!", getContext());

                    }

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


                if (!listaPitanja.contains(temp)) {
                    Pitanje dodaj = listaPitanja.get(listaPitanja.size() - 1);
                    listaPitanja.remove(listaPitanja.size() - 1);
                    listaPitanja.add(temp);
                    listaPitanja.add(dodaj);
                    adapterPitanja.notifyDataSetChanged();
                }
            }
        });

        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connectionFlag = connectionCheck();

                if (validationCkeckNaziv() && validationCheckKategorija()) {

                    //sve ok
                    clear();
                    trenutniKviz.setNaziv(etNaziv.getText().toString());
                    trenutniKviz.setPitanja(listaPitanja);
                    trenutniKviz.setKategorija(trenutnaKategorija);
                    listaPitanja.removeIf(pitanje -> pitanje.getNaziv().equals("Dodaj pitanje"));

                    adapterPitanja.notifyDataSetChanged();
                    if (connectionFlag) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Povratni kviz", trenutniKviz);
                        returnIntent.putExtra("Povratna kategorija", trenutnaKategorija);
                        returnIntent.putExtra("Povratne kategorije", listaKategorija);
                        returnIntent.putExtra("Povratna pitanja", listaPitanja);


                        InsertUBazu insertUBazu1 = new InsertUBazu();
                        insertUBazu1.setToken(token);
                        if (isPatch) {
                            insertUBazu1.setMethod("PATCH");
                        } else {
                            insertUBazu1.setMethod("POST");
                        }
                        insertUBazu1.setNazivKolekcije("Kategorije");
                        insertUBazu1.setKategorija(trenutniKviz.getKategorija());
                        insertUBazu1.setKviz(trenutniKviz);
                        insertUBazu1.execute();


                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        DodajKvizAkt.dialogIspis("Zabranjeno dodavanje kviza u offline režimu!", getContext());
                    }
                }

            }
        });

        btnImportKviz.setOnClickListener(new View.OnClickListener() {                               //importujemo kviz
            @Override
            public void onClick(View v) {
                searchTextDocuments();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Pitanje povratnoPitanje;
        Kategorija povratnaKategorija;

        if (requestCode == 3) {                                 // povratak iz dodaj pitanje aktivnost
            if (resultCode == RESULT_OK) {
                povratnoPitanje = data.getParcelableExtra("Povratno pitanje");
                povratniOdgovori = data.getStringArrayListExtra("Odgovori");
                Pitanje zamjena = listaPitanja.get(listaPitanja.size() - 1);

                listaPitanja.set(listaPitanja.size() - 1, povratnoPitanje);
                listaPitanja.add(zamjena);

                adapterPitanja.notifyDataSetChanged();
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {                      //povratak iz dodaj kategoriju aktivnost

                povratnaKategorija = data.getParcelableExtra("Povratna kategorija");

                if (povratnaKategorija != null) {
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

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (data != null) {
                uri = data.getData();

                if (validationDatoteka(uri)) {

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        BufferedReader reader = null;
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String fetcher = "";
                        String[] lineFile;
                        int linePosition = 0;

                        if (inputStream != null) {
                            for (; ; ) {
                                fetcher = reader.readLine();
                                if (fetcher == null) {                                                               //dosli smo do kraja datoteke
                                    break;
                                }
                                if (fetcher.equals("")) {
                                    continue;
                                }
                                lineFile = fetcher.split(",");                                                 // u pitanju je csv datoteka

                                if (linePosition == 0) {                                                            // prvi red datoteke
                                    etNaziv.setText(lineFile[0]);
                                    Kategorija trenutna = new Kategorija(lineFile[1], "694");                   // proizvoljan id kategorije

                                    if (listaKategorija.contains(trenutna)) {
                                        spKategorije.setSelection(listaKategorija.indexOf(trenutna));
                                    } else {

                                        Kategorija zamjena = listaKategorija.get(listaKategorija.size() - 1);            //Nova kategorija u pitanju
                                        listaKategorija.remove(listaKategorija.size() - 1);

                                        trenutnaKategorija = trenutna;
                                        listaKategorija.add(trenutna);
                                        listaKategorija.add(zamjena);

                                        adapterKategorija.notifyDataSetChanged();
                                        spKategorije.setSelection(listaKategorija.indexOf(trenutnaKategorija));
                                    }
                                } else {
                                    Pitanje trenutno = new Pitanje(lineFile[0], lineFile[0], lineFile[2], null);
                                    int brojOdgovora = Integer.parseInt(lineFile[1].replaceAll("\\s+", ""));
                                    ArrayList<String> lineOdgovori = new ArrayList<>();
                                    for (int i = 0; i < brojOdgovora; i++) {
                                        lineOdgovori.add(lineFile[3 + i]);
                                    }

                                    trenutno.setOdgovori(lineOdgovori);
                                    trenutno.setTacan(lineOdgovori.get(Integer.parseInt(lineFile[2])));
                                    listaPitanja.add(listaPitanja.size() - 1, trenutno);

                                    InsertUBazu insertUBazu = new InsertUBazu();
                                    insertUBazu.setToken(token);
                                    insertUBazu.setMethod("POST");
                                    insertUBazu.setNazivKolekcije("Pitanja");
                                    insertUBazu.setPitanje(trenutno);
                                    insertUBazu.setOdgovori((ArrayList<String>) trenutno.getOdgovori());
                                    insertUBazu.execute();
                                    adapterPitanja.notifyDataSetChanged();
                                }
                                linePosition++;
                            }
                        }
                        inputStream.close();

                    } catch (IOException e) {
                        // do nothing
                    }
                }
            }
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("Povratne kategorije", listaKategorija);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialize() {

        spKategorije = findViewById(R.id.spKategorije);
        lvDodanaPitanja = findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = findViewById(R.id.lvMogucaPitanja);
        etNaziv = findViewById(R.id.etNaziv);
        btnDodajKviz = findViewById(R.id.btnDodajKviz);
        btnImportKviz = findViewById(R.id.btnImportKviz);

        listaMogucihPitanja = new ArrayList<>();


        Intent intent = getIntent();
        trenutniKviz = intent.getParcelableExtra("Pressed kviz");
        listaKategorija = intent.getParcelableArrayListExtra("Moguce kategorije");
        listaKvizova = intent.getParcelableArrayListExtra("Kvizovi");
        trenutnaKategorija = intent.getParcelableExtra("Trenutna kategorija");
        listaPitanja = intent.getParcelableArrayListExtra("Pitanja kviza");
        if (listaPitanja == null) listaPitanja = new ArrayList<>();
        listaMogucihPitanja = intent.getParcelableArrayListExtra("Moguca pitanja");
        isPatch = intent.getBooleanExtra("PATCH", false);

        trenutniKviz.setKategorija(trenutnaKategorija);
        trenutniKviz.setPitanja(listaPitanja);


        if (listaPitanja != null) {
            if (listaPitanja.size() > 1) {
                listaPitanja.removeIf(pitanje -> pitanje.getNaziv().equals("Dodaj pitanje"));
            }
            listaPitanja.add(new Pitanje("Dodaj pitanje", "", "", null));
        }


        ArrayList<String> naziviPitanjaTrenutnog = new ArrayList<>();
        if (listaPitanja.size() > 1) {
            for (Pitanje pitanje : trenutniKviz.getPitanja()) {
                naziviPitanjaTrenutnog.add(pitanje.getNaziv());
            }

            listaMogucihPitanja.removeIf(pitanje -> naziviPitanjaTrenutnog.contains(pitanje.getNaziv()));
        }
        if (listaMogucihPitanja == null) {
            listaMogucihPitanja = new ArrayList<>();
        }
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
        if (trenutnaKategorija == null || !trenutnaKategorija.getId().equals("-2") && Integer.parseInt(trenutnaKategorija.getId()) < 0) {
            Toast.makeText(this, "Unesite kategoriju kviza", Toast.LENGTH_SHORT).show();
            this.getWindow().getDecorView().findViewById(R.id.spKategorije).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }

        return correct;
    }

    private void clear() {
        spKategorije.setBackgroundResource(android.R.drawable.spinner_background);
    }

    private void searchTextDocuments() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean validationDatoteka(Uri uri) {                            // validacija datoteke

        boolean result = true;

        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String fetcher = "";
        String[] lineFile;
        int linePosition = 0;
        ArrayList<String> naziviPitanjaUDatoteci = new ArrayList<>();

        if (inputStream != null) {
            for (; ; ) {
                try {
                    fetcher = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fetcher == null) {                                                   //dosli smo do kraja datoteke
                    break;
                }
                if (fetcher.equals("")) {
                    continue;
                }
                lineFile = fetcher.split(",");                                                // u pitanju je csv datoteka

                if (linePosition == 0) {                                                             // prvi red datoteke

                    if (lineFile.length != 3) {
                        dialogIspis("Datoteka kviza kojeg importujete nema ispravan format!", this);
                        return false;
                    }

                    final String potvrda = lineFile[0];
                    if (listaKvizova.stream().anyMatch(kviz -> kviz.getNaziv().equals(potvrda))) {         // imamo li vec isti kviz
                        dialogIspis("Kviz kojeg importujete već postoji!", this);
                        return false;
                    }

                    int brojPitanja = Integer.parseInt(lineFile[2].replaceAll("\\s+", ""));
                    int brojLinija = fileLinesCount(uri);


                    if (brojLinija - 1 != brojPitanja) {
                        dialogIspis("Kviz kojeg importujete ima neispravan broj pitanja!", this);
                        return false;
                    }

                } else {

                    int brojOdgovora = Integer.parseInt(lineFile[1].replaceAll("\\s+", ""));
                    int tacanOdgovor = Integer.parseInt(lineFile[2].replaceAll("\\s+", ""));

                    if (lineFile.length < 4) {
                        dialogIspis("Datoteka kviza kojeg importujete nema ispravan format!", this);
                        return false;
                    }
                    if (brojOdgovora + 3 != lineFile.length) {
                        dialogIspis("Kviz kojeg importujete ima neispravan broj odgovora!", this);
                        return false;
                    }
                    if (tacanOdgovor < 0 || tacanOdgovor >= lineFile.length - 3) {
                        dialogIspis("Kviz kojeg importujete ima neispravan index tačnog odgovora!", this);
                        return false;
                    }
                    final String nazivTrenutnogPitanja = lineFile[0];
                    if (listaPitanja.stream().anyMatch(pitanje -> pitanje.getNaziv().equals(nazivTrenutnogPitanja))
                            || naziviPitanjaUDatoteci.contains(nazivTrenutnogPitanja)) {
                        dialogIspis("U datoteci se nalaze postojeća pitanja!", this);
                        return false;
                    }

                    ArrayList<String> listaOdgovora = new ArrayList<>();

                    for (int i = 3; i < lineFile.length; i++) {
                        if (listaOdgovora.contains(lineFile[i])) {
                            dialogIspis("Pitanje u kvizu ima iste odgovore!", this);
                            return false;
                        }
                        listaOdgovora.add(lineFile[i]);
                    }
                    // provjeriti sta treba raditi ukoliko imamo vise istih pitanja ili odgovora
                    naziviPitanjaUDatoteci.add(nazivTrenutnogPitanja);
                }
                linePosition++;
            }
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void dialogIspis(String message, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Upozorenje!");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();                                                              // otpusti upozorenje
                    }
                });
        alertDialog.show();
    }

    private int fileLinesCount(Uri uri) {
        // brojimo redove
        int counter = 0;
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String temp = reader.readLine();
            while (temp != null) {
                if (!temp.equals("")) {
                    counter++;
                }
                temp = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            //do nothing
        }

        return counter;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean connectionCheck() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

        if (networkCapabilities != null)
            return moguDodatiKategoriju = moguDodatiPitanje =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        return moguDodatiKategoriju = moguDodatiPitanje = false;

    }
}
