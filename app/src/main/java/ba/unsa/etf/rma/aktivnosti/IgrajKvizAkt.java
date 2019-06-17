package ba.unsa.etf.rma.aktivnosti;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.OnFragmentInteractionListener, InformacijeFrag.OnFragmentInteractionListener {

    private static Context context;
    private InformacijeFrag informacijeFrag;
    private PitanjeFrag pitanjeFrag;
    private Kviz trenutniKviz;
    private ArrayList<Pitanje> listaPitanja;
    private BroadcastReceiver broadcastReceiver;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_igraj_kviz_akt);

        FragmentManager manager = getSupportFragmentManager();

        trenutniKviz = null;
        Intent intent = getIntent();
        trenutniKviz = intent.getParcelableExtra("Odabrani kviz");
        listaPitanja = intent.getParcelableArrayListExtra("Pitanja kviza");
        trenutniKviz.setPitanja(listaPitanja);

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

        if (!trenutniKviz.getPitanja().isEmpty()) {
            broadcastReceiver = generateBroadcastReceiver();
            this.registerReceiver(broadcastReceiver, new IntentFilter("Alarm"));
            pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("Alarm"), 0);

            alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, (int) (dajBrojMinuta(trenutniKviz) * 60));
     //       int test = (int) (dajBrojMinuta());
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm se okida za " + dajBrojMinuta(trenutniKviz)  + " minuta",Toast.LENGTH_LONG).show();
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

    private BroadcastReceiver generateBroadcastReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DodajKvizAkt.dialogIspis("Time is up!", IgrajKvizAkt.getContext());
            }
        };
        return receiver;
    }

    private static Context getContext() {
        return context;
    }

    public static double dajBrojMinuta(Kviz trenutniKviz) {
        int brojPitanja = trenutniKviz.getPitanja().size();
        if (brojPitanja % 2 == 1)
            return brojPitanja / 2. + 0.5;
        return brojPitanja / 2.;
    }

    @Override
    public void onBackPressed() {

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);
        if (!trenutniKviz.getPitanja().isEmpty()) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onBackPressed();

    }
}
