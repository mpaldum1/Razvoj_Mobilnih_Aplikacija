package ba.unsa.etf.rma.aktivnosti;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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


public class KvizoviAkt extends AppCompatActivity implements ListaFrag.OnFragmentInteractionListener, OnFragmentInteractionListener {

    private Spinner spPostojeceKategorije;
    private ListView lwkvizovi;

    private ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private KvizAdapter adapterKviz;
    private KategorijaAdapter adapterKategorija;

    private ArrayList<Kviz> filterListKvizova = new ArrayList<>();
    private Kategorija trenutnaKategorija;

    private Kviz dodajKviz;

    private ListaFrag listFragment = new ListaFrag();
    private DetailFrag detailFrag = new DetailFrag();

    static public String token = "";
    static public String projectID = "rmaspirala-1bc9b";
    private TokenGenerator tokenGenerator;
    private ArrayList<Pitanje> mogucaPitanja = new ArrayList<>();
    private InsertUBazu insertUBazu = new InsertUBazu();
    private boolean isPatch = false;
    private ArrayList<String> naziviMogucihPitanja = new ArrayList<>();
    FetchKategorijeBaza fetchKategorijeBaza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);


        dodajKviz = new Kviz("Dodaj kviz", null, new Kategorija("", Integer.toString(R.drawable.plus)));
        listaKvizova.add(dodajKviz);


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

                    if (trenutnaKategorija.getId().equals("-1")) {                                              //pritisnuto Kategorije
                        // empty
                    } else {
                        Toast.makeText(KvizoviAkt.this, clickedKategorijaNaziv, Toast.LENGTH_SHORT).show();

                        if (trenutnaKategorija.getId().equals("-2")) {// Pritisnuto "Sve"

                            filterListKvizova.removeAll(filterListKvizova);

                            filterListKvizova.addAll(listaKvizova);
                            for (Kviz currentKviz : listaKvizova) {
                                if (!filterListKvizova.contains(currentKviz)) {
                                    filterListKvizova.add(currentKviz);
                                }
                            }

                        } else {

                            filterListKvizova.removeAll(filterListKvizova);
                            // Filtriramo

                            for (Kviz currentKviz : listaKvizova) {
                                if (currentKviz.getKategorija().getId().equals((trenutnaKategorija.getId()))) {
                                    filterListKvizova.add(currentKviz);
                                }
                            }
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
                    Kviz trenutni = adapterKviz.getItem(position);

                    isPatch = true;

                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    intent.putExtra("Pressed kviz", trenutni);
                    intent.putExtra("Moguce kategorije", listaKategorija);
                    intent.putExtra("Kvizovi", listaKvizova);
                    intent.putExtra("Trenutna kategorija", trenutni.getKategorija());
                    intent.putExtra("Pitanja kviza", trenutni.getPitanja());
                    intent.putExtra("Token", token);
                    startActivityForResult(intent, 1);

                    return true;
                }
            });

            lwkvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {                           // listView listener
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Kviz trenutni = adapterKviz.getItem(position);
                    Intent intent = null;

                    if (trenutni.getNaziv().equals("Dodaj kviz")) {
                        intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                        intent.putExtra("Pressed kviz", trenutni);
                        intent.putExtra("Moguce kategorije", listaKategorija);
                        intent.putExtra("Kvizovi", listaKvizova);
                        intent.putExtra("Trenutna kategorija", trenutni.getKategorija());
                        intent.putExtra("Pitanja kviza", trenutni.getPitanja());

                        startActivityForResult(intent, 1);
                    } else {
                        intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                        intent.putExtra("Odabrani kviz", trenutni);
                        intent.putExtra("Pitanja kviza", trenutni.getPitanja());
                        startActivityForResult(intent, 2);
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

        lwkvizovi.setAdapter(adapterKviz);
        spPostojeceKategorije.setAdapter(adapterKategorija);

        new TokenGenerator(this, token).execute();

        fetchKategorijeBaza = new FetchKategorijeBaza() {
            @Override
            protected void onPostExecute(Void aVoid) {

                listaKategorija.addAll(fetchKategorijeBaza.getKategorije());
                adapterKviz.notifyDataSetChanged();

                FetchPitanjaBaza fetchPitanjaBaza = new FetchPitanjaBaza() {

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        listaPitanja = getMogucaPitanja();
                        for (Pitanje trenutno : listaPitanja) {
                            if (!naziviMogucihPitanja.contains(trenutno.getNaziv())) {
                                naziviMogucihPitanja.add(trenutno.getNaziv());
                            }
                        }
                        FetchKvizove fetchKvizove = new FetchKvizove() {
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                listaKvizova.addAll(getKvizovi());
                                filterListKvizova.addAll(getKvizovi());
                                filterListKvizova.add(dodajKviz);
                                adapterKviz.notifyDataSetChanged();
                            }
                        };
                        fetchKvizove.setIdKategorije(" Geografija");
                        fetchKvizove.setMogucaPitanja(listaPitanja);
                        fetchKvizove.setKategorije(getKategorije());
                        fetchKvizove.setKvizovi(listaKvizova);
                        fetchKvizove.execute();
                    }
                };
                fetchPitanjaBaza.execute();
            }
        };
        fetchKategorijeBaza.execute();
        adapterKviz.notifyDataSetChanged();
    }

    @Override
    public void onInputA(String nazivKategorije) {
        detailFrag.filtrirajIPrikazi(nazivKategorije);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

   /* public class Fetch extends AsyncTask<String, Integer, Void> {

        private String urlString;
        private final String projectID = "rmaspirala-1bc9b";
        private String tipKolekcije;

        private FetchKvizove fetchKvizove = new FetchKvizove();
        private FetchPitanjaBaza fetchPitanjaBaza = new FetchPitanjaBaza();
        private FetchKategorijeBaza fetchKategorijeBaza = new FetchKategorijeBaza();

        public void setTipKolekcije(String tipKolekcije) {
            this.tipKolekcije = tipKolekcije;
        }


        @Override
        protected Void doInBackground(String... strings) {
            urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/" +
                    "(default)/documents/" + tipKolekcije + "?access_token=" + token;


            try {
                URL request = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String result = streamToStringConvertor(inputStream);

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("documents");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject name = jsonArray.getJSONObject(i);
                    String id = name.getString("name");
                    JSONObject kviz = name.getJSONObject("fields");
                    JSONArray items = kviz.getJSONObject("pitanja").getJSONObject("arrayValue").getJSONArray("values");  //lista pitanja kviza
                    for (int j = 0; j < items.length(); j++) {
                        naziviMogucihPitanja.add(items.getJSONObject(j).getString("stringValue"));
                    }
                }
                switch (tipKolekcije) {
                    case "Kvizovi":
                        fetchKvizove.fetchKvizoveBaze(jsonArray);
                        listaKvizova = fetchKvizove.getKvizovi();
                        if (adapterKviz != null)
                            adapterKviz.notifyDataSetChanged();
                        break;
                    case "Kategorije":
                        fetchKategorijeBaza.fetchKategorijeBaze(jsonArray);
                        listaKategorija = fetchKategorijeBaza.getKategorije();
                        if (adapterKategorija != null)
                            adapterKategorija.notifyDataSetChanged();
                        break;

                    case "Pitanja":
                        fetchPitanjaBaza.fetchPitanjaBaze(jsonArray);
                        listaPitanja = fetchPitanjaBaza.getMogucaPitanja();
                        break;

                    default:
                        break;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    */

}
