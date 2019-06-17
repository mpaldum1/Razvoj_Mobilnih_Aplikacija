package ba.unsa.etf.rma.aktivnosti;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KategorijaAdapter;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.DetailFrag.OnFragmentInteractionListener;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.servisi.FetchKategorijeBaza;
import ba.unsa.etf.rma.servisi.FetchKvizove;
import ba.unsa.etf.rma.servisi.FetchPitanjaBaza;
import ba.unsa.etf.rma.servisi.InsertUBazu;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt.dajBrojMinuta;


public class KvizoviAkt extends AppCompatActivity implements ListaFrag.OnFragmentInteractionListener, OnFragmentInteractionListener {

    private Spinner spPostojeceKategorije;
    private ListView lwkvizovi;

    private ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private KvizAdapter adapterKviz;
    private KategorijaAdapter adapterKategorija;
    private ArrayList<Kviz> helperLista = new ArrayList<>();

    private ArrayList<Kviz> filterListKvizova = new ArrayList<>();
    private Kategorija trenutnaKategorija;

    private Kviz dodajKviz;

    private ListaFrag listFragment = new ListaFrag();
    private DetailFrag detailFrag = new DetailFrag();

    static public String token = "";
    static public String projectID = "rmaspirala-1bc9b";

    private InsertUBazu insertUBazu = new InsertUBazu();
    private Boolean isPatch = false;
    private ArrayList<String> naziviMogucihPitanja = new ArrayList<>();
    private FetchKategorijeBaza fetchKategorijeBaza;

    private boolean calendarHasPermssion = false;
    private boolean isConnected = false;
    private int minuteDoEventa;

    private Context context;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);
        context = this;


        dodajKviz = new Kviz("Dodaj kviz", null, new Kategorija("", Integer.toString(R.drawable.plus)));
        calendarHasPermssion = doIHavePermission(1, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        isConnected = connectionCheck();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("Prvi", "Usao u landscape");
            fragmentCall();

        } else {

            init();


            spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {          // spinner listener
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    trenutnaKategorija = (Kategorija) parent.getItemAtPosition(position);
                    String clickedKategorijaNaziv = trenutnaKategorija.getNaziv();

                    if(!trenutnaKategorija.getNaziv().equals("Svi")) {
                        FetchKvizove fetchKvizove = new FetchKvizove() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            protected void onPostExecute(Void aVoid) {

                                helperLista = new ArrayList<>(getKvizovi());
                                filterListKvizova.removeIf(kviz -> !helperLista.contains(kviz));
                                adapterKviz.notifyDataSetChanged();

                            }
                        };
                        fetchKvizove.setMogucaPitanja(listaPitanja);
                        fetchKvizove.setKategorije(listaKategorija);
                        fetchKvizove.setIdKategorije(trenutnaKategorija.getNaziv());
                        fetchKvizove.setKvizovi(listaKvizova);
                        fetchKvizove.execute();
                    }

                    if (trenutnaKategorija.getId().equals("-1")) {                                              //pritisnuto Kategorije
                        // empty
                    } else {
                        Toast.makeText(KvizoviAkt.this, clickedKategorijaNaziv, Toast.LENGTH_SHORT).show();

                        if (trenutnaKategorija.getId().equals("-2")) {// Pritisnuto "Sve"

                            filterListKvizova.clear();
                            filterListKvizova.addAll(listaKvizova);
                            adapterKviz.notifyDataSetChanged();

                        } else {

                            filterListKvizova.clear();
                            // Filtriramo

                            for (Kviz currentKviz : listaKvizova) {
                                if (currentKviz.getKategorija().getId().equals((trenutnaKategorija.getId()))) {
                                    filterListKvizova.add(currentKviz);
                                }
                            }
                            if (!filterListKvizova.contains(dodajKviz))
                                filterListKvizova.add(dodajKviz);

                            adapterKviz.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            lwkvizovi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    isConnected = connectionCheck();
                    if (isConnected) {
                        Kviz trenutni = adapterKviz.getItem(position);

                        if (!trenutni.getNaziv().equals("Dodaj kviz"))
                            isPatch = true;

                        Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                        intent.putExtra("Pressed kviz", trenutni);
                        intent.putExtra("Moguce kategorije", listaKategorija);
                        intent.putExtra("Kvizovi", listaKvizova);
                        intent.putExtra("Trenutna kategorija", trenutni.getKategorija());
                        if (trenutni.getPitanja() == null)
                            trenutni.setPitanja(new ArrayList<>());
                        intent.putExtra("Pitanja kviza", trenutni.getPitanja());
                        intent.putExtra("Token", token);
                        intent.putExtra("Moguca pitanja", listaPitanja);
                        intent.putExtra("PATCH", isPatch);
                        startActivityForResult(intent, 1);
                    } else {
                        DodajKvizAkt.dialogIspis("Zabranjeno editovanje/dodavanje kvizova u offline režimu!", getContext());
                    }

                    return true;
                }
            });

            lwkvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {                           // listView listener
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Kviz trenutni = adapterKviz.getItem(position);
                    Intent intent = null;

                    if (trenutni.getNaziv().equals("Dodaj kviz")) {
                        isConnected = connectionCheck();
                        if (isConnected) {
                            intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                            intent.putExtra("Pressed kviz", trenutni);
                            intent.putExtra("Moguce kategorije", listaKategorija);
                            intent.putExtra("Kvizovi", listaKvizova);
                            intent.putExtra("Trenutna kategorija", trenutni.getKategorija());
                            if (trenutni.getPitanja() == null)
                                trenutni.setPitanja(new ArrayList<>());
                            intent.putExtra("Pitanja kviza", trenutni.getPitanja());
                            intent.putExtra("Moguca pitanja", listaPitanja);
                            startActivityForResult(intent, 1);
                        } else {
                            DodajKvizAkt.dialogIspis("Zabranjeno dodavanje kvizova u offline režimu!", getContext());
                        }


                    } else {
                        if (!trenutni.getPitanja().isEmpty() && hasEventInYMinutes(trenutni)) {
                            if(minuteDoEventa != 0) {
                                DodajKvizAkt.dialogIspis("Imate događaj u kalendaru za " + minuteDoEventa +          // otvaramo Alert dialog
                                        " minuta! ", getContext());
                            }
                            else {
                                DodajKvizAkt.dialogIspis("Imate događaj u toku!", getContext());
                            }
                        } else {
                            intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                            intent.putExtra("Odabrani kviz", trenutni);
                            if (trenutni.getPitanja() == null)
                                trenutni.setPitanja(new ArrayList<>());
                            intent.putExtra("Pitanja kviza", trenutni.getPitanja());
                            startActivityForResult(intent, 2);
                        }
                    }
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {                         //povratak iz dodaj kviz aktivnosti
            if (resultCode == RESULT_OK) {

                assert data != null;
                Kviz povratniKviz = data.getParcelableExtra("Povratni kviz");
                Kategorija povratnaKategorija = data.getParcelableExtra("Povratna kategorija");
                ArrayList<Kategorija> povratneKategorije = data.getParcelableArrayListExtra("Povratne kategorije");
                ArrayList<Pitanje> povratnaPitanja = data.getParcelableArrayListExtra("Povratna pitanja");


                Kategorija zamjena = listaKategorija.get(listaKategorija.size() - 1);            //'Svi ostaje na kraju
                listaKategorija.remove(listaKategorija.size() - 1);

                listaKategorija.add(zamjena);
                adapterKategorija.notifyDataSetChanged();


                if (povratniKviz != null) {

                    povratniKviz.setKategorija(povratnaKategorija);
                    povratniKviz.setPitanja(povratnaPitanja);

                    listaKvizova.removeIf(kviz -> kviz.getNaziv().equals(povratniKviz.getNaziv()));
                    filterListKvizova.removeIf(kviz -> kviz.getNaziv().equals(povratniKviz.getNaziv()));

                    Kviz zamjenski = listaKvizova.get(listaKvizova.size() - 1);

                    listaKvizova.set(listaKvizova.size() - 1, povratniKviz);
                    filterListKvizova.set(filterListKvizova.size() - 1, povratniKviz);

                    listaKvizova.add(zamjenski);
                    filterListKvizova.add(zamjenski);

                    adapterKviz.notifyDataSetChanged();

                    insertUBazu = new InsertUBazu();
                    insertUBazu.setToken(token);
                    insertUBazu.setNazivKolekcije("Kvizovi");
                    insertUBazu.setKviz(povratniKviz);

                    if (isPatch) {
                        insertUBazu.setMethod("PATCH");

                        insertUBazu.setIdStarogKviza(povratniKviz.getNaziv());
                    } else {

                        insertUBazu.setPitanja(povratnaPitanja);
                        insertUBazu.setMethod("POST");
                    }
                    isPatch = false;
                    insertUBazu.execute();
                }


            }

            trenutnaKategorija = listaKategorija.get(1);
            spPostojeceKategorije.setSelection(1);
        }
    }


    public void init() {

        spPostojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);                 //pripremamo pocetno stanje
        lwkvizovi = (ListView) findViewById(R.id.lvKvizovi);

        trenutnaKategorija = new Kategorija("Svi", "-2");

        listaKategorija.add(new Kategorija("Kategorije", "-1"));
        listaKategorija.add(trenutnaKategorija);

        adapterKviz = new KvizAdapter(this, R.layout.row_view, filterListKvizova);                  // postavljamo adaptere
        adapterKategorija = new KategorijaAdapter(this, listaKategorija);
        filterListKvizova.add(dodajKviz);

        lwkvizovi.setAdapter(adapterKviz);
        spPostojeceKategorije.setAdapter(adapterKategorija);

        if (isConnected)
            new TokenGenerator(this, token).execute();

        fetchKategorijeBaza = new FetchKategorijeBaza() {
            @Override
            protected void onPostExecute(Void aVoid) {

                listaKategorija.addAll(fetchKategorijeBaza.getKategorije());

                FetchPitanjaBaza fetchPitanjaBaza = new FetchPitanjaBaza() {

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        listaPitanja = new ArrayList<>(getMogucaPitanja());
                        for (Pitanje trenutno : listaPitanja) {
                            if (!naziviMogucihPitanja.contains(trenutno.getNaziv())) {
                                naziviMogucihPitanja.add(trenutno.getNaziv());
                            }
                        }
                        FetchKvizove fetchKvizove = new FetchKvizove() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                for(Kviz trenutni: getKvizovi()){
                                    listaKvizova.add(trenutni);
                                }
                       //         listaKvizova.addAll(getKvizovi());


                                if (!listaKvizova.contains(dodajKviz))
                                    listaKvizova.add(dodajKviz);

                                adapterKviz.notifyDataSetChanged();
                                spPostojeceKategorije.setSelection(1);

                            }
                        };
                        fetchKvizove.setMogucaPitanja(listaPitanja);
                        fetchKvizove.setKategorije(getKategorije());
                        fetchKvizove.setIdKategorije("Svi");
                        fetchKvizove.setKvizovi(new ArrayList<>());
                        fetchKvizove.execute();

                    }
                };
                fetchPitanjaBaza.execute();
            }
        };
        fetchKategorijeBaza.execute();
    }

    public static boolean moguDodatiPitanje;
    public static boolean moguDodatiKategoriju;

    @RequiresApi(api = Build.VERSION_CODES.M)
        // provjera konekcije
    boolean connectionCheck() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

        if (networkCapabilities != null)
            return moguDodatiKategoriju = moguDodatiPitanje =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        return moguDodatiKategoriju = moguDodatiPitanje = false;

    }

    @Override
    public void onInputA(String nazivKategorije) {
        detailFrag.filtrirajIPrikazi(nazivKategorije);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public Context getContext() {
        return context;
    }

    private void fragmentCall() {
        FragmentManager manager = getFragmentManager();
        FrameLayout lijeviFragment = findViewById(R.id.listPlace);
        Log.e("Drugi", "fragmentCall");


        FragmentTransaction transaction = manager.beginTransaction();
        // u prosirenom modu smo
        boolean sirokiL = true;
        // listFragment = (ListFragment) manager.findFragmentById(R.id.listPlace);

        Log.e("Treci", "ldetalji != null");

        if (listFragment == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelableArrayList("Kvizovi", listaKvizova);
            arguments.putParcelableArrayList("Kategorije", listaKategorija);
            listFragment.setArguments(arguments);
        }
        transaction.replace(R.id.listPlace, listFragment);

        // detailFrag = (DetailFrag) manager.findFragmentById(R.id.detailPlace);

        if (detailFrag == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelableArrayList("Kvizovi", listaKvizova);
            arguments.putParcelableArrayList("Kategorija", listaKategorija);
            detailFrag.setArguments(arguments);
        }
        transaction.add(R.id.detailPlace, detailFrag);
        transaction.commit();
    }

    public static class TokenGenerator extends AsyncTask<String, Void, String> {

        Context context;
        GoogleCredential credential;

        private WeakReference<KvizoviAkt> activityReference;

        public TokenGenerator(KvizoviAkt context, String token) {
            this.context = context;

            activityReference = new WeakReference<>(context);
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                InputStream is = context.getResources().openRawResource(R.raw.secret);
                credential = GoogleCredential.fromStream(is).createScoped(
                        Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

                credential.refreshToken();


                Log.e("TOKEN", token);
            } catch (IOException e) {
                e.printStackTrace();
            }
            token = credential.getAccessToken();
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            KvizoviAkt activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            token = s;

        }
    }


    private boolean doIHavePermission(int callback, String... permissions) {
        boolean result = true;
        for (String temp : permissions) {
            result = ContextCompat.checkSelfPermission(this, temp) == PERMISSION_GRANTED && result;
        }
        if (!result) {
            ActivityCompat.requestPermissions(this, permissions, callback);
        }
        return result;
    }

    private static final String[] fields = {
            CalendarContract.Calendars.NAME,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.TITLE
    };

    private boolean hasEventInYMinutes(Kviz trenutniKviz) {
        if (!calendarHasPermssion) return false;
        int brojSekundi = (int) (dajBrojMinuta(trenutniKviz) * 60);

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/events"), fields,
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            ArrayList<String> names = new ArrayList<>();
            int size = cursor.getCount();

            long currentTimeMS = System.currentTimeMillis();

            for (int i = 0; i < size; i++) {
                names.add(cursor.getString(3));
                long start = Long.parseLong(cursor.getString(1));
                long end = Long.parseLong(cursor.getString(2));

                if (start < currentTimeMS && end > currentTimeMS) {
                    minuteDoEventa = 0;                         // event se upravo desava
                    return true;
                }
                if (start > currentTimeMS && start < currentTimeMS + brojSekundi * 1000) {
                    minuteDoEventa = (int) ((start - currentTimeMS) / (60. * 1000) + 0.5) ;
                    if (minuteDoEventa < dajBrojMinuta(trenutniKviz))
                        return true;
                }
                cursor.moveToNext();
            }

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length != 0 && grantResults[0] == PERMISSION_GRANTED)
                calendarHasPermssion = true;
        } else {
            calendarHasPermssion = false;
        }
    }
}
