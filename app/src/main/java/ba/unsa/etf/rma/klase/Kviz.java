package ba.unsa.etf.rma.klase;

import java.util.ArrayList;

public class Kviz {
    String naziv;
    ArrayList<Pitanje> pitanja = new ArrayList<>();
    Kategorija kategorija;

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void dodajPitanje(Pitanje pitanje) {
        if (!pitanja.contains(pitanje))
            pitanja.add(pitanje);
    }
}
