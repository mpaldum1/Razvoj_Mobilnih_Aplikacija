package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private EditText etNaziv, etIkona;
    private Button btnDodajIkonu, btnDodajKategoriju;
    ArrayList<Kategorija> listaKategorija;

    private Icon[] selectedIcons;
    private Kategorija trenutnaKategorija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);


        init();
        Intent intent = getIntent();
        listaKategorija = intent.getParcelableArrayListExtra("Pressed kategorije");
        etIkona.setFocusable(false);                // onemogucen unos

        IconDialog iconDialog = new IconDialog();

        btnDodajIkonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
            }
        });

        btnDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validationCkeck()) {
                    clear();
                    // uredu je sve

                    trenutnaKategorija.setNaziv(etNaziv.getText().toString());
                    trenutnaKategorija.setId(etIkona.getText().toString());

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Povratna kategorija", trenutnaKategorija);

                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void init() {
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etIkona = (EditText) findViewById(R.id.etIkona);
        btnDodajIkonu = (Button) findViewById(R.id.btnDodajIkonu);
        btnDodajKategoriju = (Button) findViewById(R.id.btnDodajKategoriju);

        trenutnaKategorija = new Kategorija("", "");

    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        etIkona.setText(Integer.toString(icons[0].getId()));
    }

    private boolean validationCkeck() {

        boolean correct = true;
        String ikona = etIkona.getText().toString();
        String naziv = etNaziv.getText().toString();

        if (ikona.equals("")) {
            this.getWindow().getDecorView().findViewById(R.id.etIkona).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }
        if (naziv.equals("")) {
            this.getWindow().getDecorView().findViewById(R.id.etNaziv).setBackgroundResource(R.color.colorRedValidation);
            correct = false;
        }

        if (listaKategorija != null) {
            for (Kategorija trenutna : listaKategorija) {
                if (trenutna.getNaziv().equals(naziv) || trenutna.getId().equals(ikona)) {
                    correct = false;
                    break;
                }
            }
        }
        return correct;
    }

    private void clear() {
        etIkona.setBackgroundResource(android.R.drawable.edit_text);
        etNaziv.setBackgroundResource(android.R.drawable.edit_text);
    }
}
