package ba.unsa.etf.rma.klase;

import java.util.ArrayList;
import java.util.List;

public class Pitanje {
    String naziv, tekstPitanja, tacan;
    List<String> odgovori = new ArrayList<>();

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public String getTacan() {
        return tacan;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public List<String> getOdgovori() {
        return odgovori;
    }

    public void setOdgovori(List<String> odgovori) {
        this.odgovori = odgovori;
    }

    ArrayList<String> dajRandomOdgovore () {
        ArrayList<String> odgovori = new ArrayList<>();

        return odgovori;
    }
}
