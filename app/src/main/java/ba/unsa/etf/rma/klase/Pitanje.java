package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Pitanje implements Parcelable {
    String naziv, tekstPitanja, tacan;
    List<String> odgovori = new ArrayList<>();

    public Pitanje(String naziv, String tekstPitanja, String tacan, List<String> odgovori) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.tacan = tacan;
        this.odgovori = odgovori;
    }

    protected Pitanje(Parcel in) {
        naziv = in.readString();
        tekstPitanja = in.readString();
        tacan = in.readString();
        odgovori = in.createStringArrayList();
    }

    public static final Creator<Pitanje> CREATOR = new Creator<Pitanje>() {
        @Override
        public Pitanje createFromParcel(Parcel in) {
            return new Pitanje(in);
        }

        @Override
        public Pitanje[] newArray(int size) {
            return new Pitanje[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        dest.writeString(tekstPitanja);
        dest.writeString(tacan);
        dest.writeStringList(odgovori);
    }
}
