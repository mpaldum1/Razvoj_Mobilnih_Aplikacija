package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class KvizoviAkt extends AppCompatActivity implements ListaFrag.OnFragmentInteractionListener, OnFragmentInteractionListener{

    private Spinner spPostojeceKategorije;
    private ListView lwkvizovi;

    private ArrayList<Kviz> listaKvizova;
    private ArrayList<Kategorija> listaKategorija;
    private KvizAdapter adapterKviz;
    private KategorijaAdapter adapterKategorija;

    private ArrayList<Kviz> filterListKvizova;
    private Kategorija trenutnaKategorija;

    private Kviz dodajKviz;
    private boolean sirokiL = false;

    private ListFragment listFragment;
    private DetailFrag detailFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);

        init();
        FragmentManager manager = getSupportFragmentManager();
        FrameLayout ldetalji = (FrameLayout) findViewById(R.id.listPlace);

        if (ldetalji != null) {                                  // u prosirenom modu smo
            sirokiL = true;
            listFragment = (ListFragment) manager.findFragmentById(R.id.listPlace);

            if (listFragment == null) {
                listFragment = new ListFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList("Kvizovi", listaKvizova);
                arguments.putParcelableArrayList("Kategorije", listaKategorija);
                listFragment.setArguments(arguments);
                manager.beginTransaction().replace(R.id.listPlace, listFragment).commit();
            }

            detailFrag = (DetailFrag) manager.findFragmentById(R.id.detailPlace);

            if (detailFrag == null) {
                detailFrag = new DetailFrag();
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList("Kvizovi", listaKvizova);
                arguments.putParcelableArrayList("Kategorija", listaKategorija);
                detailFrag.setArguments(arguments);
                manager.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
            }

        } else {

            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            adapterKviz = new KvizAdapter(this, R.layout.row_view, filterListKvizova);                  // postavljamo adaptere
            adapterKategorija = new KategorijaAdapter(this, listaKategorija);

            lwkvizovi.setAdapter(adapterKviz);
            spPostojeceKategorije.setAdapter(adapterKategorija);


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
                            adapterKviz.notifyDataSetChanged();

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


                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    intent.putExtra("Pressed kviz", trenutni);
                    intent.putExtra("Moguce kategorije", listaKategorija);
                    intent.putExtra("Kvizovi", listaKvizova);
                    intent.putExtra("Trenutna kategorija", trenutni.getKategorija());
                    intent.putExtra("Pitanja kviza", trenutni.getPitanja());
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

                Kviz povratniKviz = data.getParcelableExtra("Povratni kviz");
                Kategorija povratnaKategorija = data.getParcelableExtra("Povratna kategorija");
                ArrayList<Kategorija> povratneKategorije = data.getParcelableArrayListExtra("Povratne kategorije");
                ArrayList<Pitanje> povratnaPitanja = data.getParcelableArrayListExtra("Povratna pitanja");


                Kategorija zamjena = listaKategorija.get(listaKategorija.size() - 1);            //'Svi ostaje na kraju
                listaKategorija.remove(listaKategorija.size() - 1);

                for (Kategorija trenutna : povratneKategorije) {
                    if (!listaKategorija.contains(trenutna) && Integer.parseInt(trenutna.getId()) >= 0) {
                        listaKategorija.add(trenutna);
                    }
                }

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
                }
            }

            trenutnaKategorija = listaKategorija.get(listaKategorija.size() - 1);
            spPostojeceKategorije.setSelection(listaKategorija.size() - 1);
        }
    }

    public void init() {

        spPostojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);                 //pripremamo pocetno stanje
        lwkvizovi = (ListView) findViewById(R.id.lvKvizovi);

        trenutnaKategorija = new Kategorija("Svi", "-2");
        listaKvizova = new ArrayList<>();
        filterListKvizova = new ArrayList<>();

        listaKategorija = new ArrayList<>();
        listaKategorija.add(new Kategorija("Kategorije", "-1"));
        listaKategorija.add(trenutnaKategorija);

        dodajKviz = new Kviz("Dodaj kviz", null, new Kategorija("", Integer.toString(R.drawable.plus)));
        listaKvizova.add(dodajKviz);
        filterListKvizova.add(dodajKviz);
    }

    @Override
    public void onInputA(String nazivKategorije) {
        detailFrag.filtrirajIPrikazi(nazivKategorije);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
