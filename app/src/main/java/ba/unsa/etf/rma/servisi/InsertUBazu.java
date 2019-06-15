package ba.unsa.etf.rma.servisi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class InsertUBazu extends AsyncTask<String, Integer, Void> {

    private String token;
    private String nazivKolekcije;
    final private String projectID = "rmaspirala-1bc9b";
    private String method;
    String urlString;

    private Kviz kviz;
    private Kategorija kategorija;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private ArrayList<String> odgovori = new ArrayList<>();
    private Pitanje pitanje;
    private int indeksTacnog = 0;
    private String idStarogKviza;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNazivKolekcije() {
        return nazivKolekcije;
    }

    public void setNazivKolekcije(String nazivKolekcije) {
        this.nazivKolekcije = nazivKolekcije;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setKviz(Kviz kviz) {
        this.kviz = kviz;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public void setIndeksTacnog(int indeksTacnog) {
        this.indeksTacnog = indeksTacnog;
    }

    public void setPitanje(Pitanje pitanje) {
        this.pitanje = pitanje;
    }

    public void setIdStarogKviza(String idStarogKviza) {
        this.idStarogKviza = idStarogKviza;
    }

    @Override
    protected Void doInBackground(String... strings) {


        if (idStarogKviza != null) {
            urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/(default)/documents/" +
                    nazivKolekcije + "?naziv=" + idStarogKviza + "?access_token=" + token;
            idStarogKviza = null;

        } else {
            urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/(default)/documents/"
                    + nazivKolekcije + "?access_token=" + token;
        }

        URL url = null;
        try {
            url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setDoOutput(true);
            request.setRequestMethod(method);

            request.setRequestProperty("Content-Type", "application/json; utf-8");
            request.setRequestProperty("Accept", "application/json");

            if (method.equals("PATCH")) {
                request.setRequestMethod("DELETE");
            }

            String jsonDocument = "";

            switch (nazivKolekcije) {

                case "Kvizovi":
                    if (kviz == null || kviz.getNaziv() == null) break;

                    Log.e("link", urlString);

                    jsonDocument = "{ \"fields\": { \"pitanja\": { \"arrayValue\": { \"values\": [";
                    int counter = 1;

                    for (Pitanje trenutno : kviz.getPitanja()) {
                        if (trenutno.getNaziv().equals(""))
                            continue;
                        if (counter < kviz.getPitanja().size() - 1)
                            jsonDocument += "{ \"stringValue\": \"" + trenutno.getNaziv() + "\"}, ";
                        else if (counter == kviz.getPitanja().size() - 1)
                            jsonDocument += "{ \"stringValue\": \"" + trenutno.getNaziv() + "\"} ";
                        ++counter;
                    }

                    jsonDocument += "]}}, \"naziv\": { \"stringValue\": \"" +
                            kviz.getNaziv() + "\"}, \"idKategorije\": { \"stringValue\": \"" + kviz.getKategorija().getNaziv() + "\"}}}";
                    break;

                case "Kategorije":
                    jsonDocument = "{ \"fields\": { \"idIkonice\": { \"integerValue\": \""
                            + kategorija.getId() + "\"}, \"naziv\": { \"stringValue\": \"" + kategorija.getNaziv() + "\"}}}";
                    break;

                case "Pitanja":
                    if (pitanje != null) {
                        pitanja.add(pitanje);
                    }

                    int positionTrue = 0;
                    for (String trenutni : pitanje.getOdgovori()) {
                        if (trenutni.equals(pitanje.getTacan())) {
                            break;
                        }
                        positionTrue++;
                    }
                    jsonDocument = "{ \"fields\": { \"naziv\": { \"stringValue\": \"" + pitanje.getNaziv() + "\"}, \"odgovori\": { " +
                            "\"arrayValue\": { \"values\": [";

                    counter = 1;
                    int total = odgovori.size();

                    for (String o : odgovori) {
                        if (counter != total) {
                            jsonDocument += "{ \"stringValue\": \"" + o + "\"}, ";
                        } else {
                            jsonDocument += "{ \"stringValue\": \"" + o + "\"} ";
                        }
                        ++counter;
                    }
                    jsonDocument += "]}}, \"indexTacnog\": { \"integerValue\": \"" + positionTrue + "\"}}}";


                    break;

                default:
                    break;
            }


            try (OutputStream os = request.getOutputStream()) {
                byte[] input = jsonDocument.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            InputStream inputStream = request.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            Log.d("Odgovor", response.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
