package ba.unsa.etf.rma.klase;

public class Kategorija {

    private  String naziv, id;

    public Kategorija(String naziv, String id) {
        this.naziv = naziv;
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        return this.id.equals(((Kategorija)obj).getId());
    }
}
