package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Spinner;

import ba.unsa.etf.rma.R;

public class KvizAkt extends AppCompatActivity {

    Spinner postojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);
    ListView kvizovi = (ListView) findViewById(R.id.lvKvizovi);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
