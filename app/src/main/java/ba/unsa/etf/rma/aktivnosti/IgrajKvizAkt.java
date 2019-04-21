package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.OnFragmentInteractionListener, InformacijeFrag.OnFragmentInteractionListener {

    private InformacijeFrag informacijeFrag;
    private PitanjeFrag pitanjeFrag;
    private Kviz trenutniKviz;
    private ArrayList<Pitanje> listaPitanja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        FragmentManager manager = getSupportFragmentManager();

        trenutniKviz = null;
        Intent intent = getIntent();
        trenutniKviz = intent.getParcelableExtra("Odabrani kviz");
        listaPitanja = intent.getParcelableArrayListExtra("Pitanja kviza");

        informacijeFrag = (InformacijeFrag) manager.findFragmentById(R.id.fragment_informacije);
        Bundle arguments;

        if (informacijeFrag == null) {
            informacijeFrag = new InformacijeFrag();
            arguments = new Bundle();
            arguments.putParcelable("Kviz", trenutniKviz);
            arguments.putParcelableArrayList("Pitanja", listaPitanja);
            informacijeFrag.setArguments(arguments);
            manager.beginTransaction().replace(R.id.fragment_informacije, informacijeFrag, informacijeFrag.getTag()).commit();
        }

        pitanjeFrag = (PitanjeFrag) manager.findFragmentById(R.id.fragment_pitanje);
        if (pitanjeFrag == null) {
            pitanjeFrag = new PitanjeFrag();
            arguments = new Bundle();
            arguments.putParcelable("Kviz", trenutniKviz);
            arguments.putParcelableArrayList("Pitanja", listaPitanja);
            pitanjeFrag.setArguments(arguments);
            manager.beginTransaction().replace(R.id.fragment_pitanje, pitanjeFrag, pitanjeFrag.getTag()).commit();
        }

    }

    @Override
    public void onInputB(String brojTacnihOdgvoora, String preostaloPitanja, String procenatTacnihOdgovora) {
        informacijeFrag.setEditTexts(brojTacnihOdgvoora, preostaloPitanja, procenatTacnihOdgovora);
    }

    @Override
    public void onInputA(CharSequence input) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
