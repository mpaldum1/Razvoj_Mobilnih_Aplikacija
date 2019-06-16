package ba.unsa.etf.rma.servisi;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.projectID;
import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.token;

public class FetchKvizove extends AsyncTask<String, Void, Void> {

    private String urlString = "";
    private String upit = "";
    private String idKategorije = "";                       // naziv kategorije jer je on jedinstven
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private Kategorija requestedKategorija;
    private ArrayList<Pitanje> mogucaPitanja = new ArrayList<>();
    private ArrayList<String> pitanjaIDMask = new ArrayList<>();

    private String nazivKolekcije = "Kvizovi";

    public void setIdKategorije(String idKategorije) {
        this.idKategorije = idKategorije;
    }

    public void setKvizovi(ArrayList<Kviz> kvizovi) {
        this.kvizovi = kvizovi;
    }

    public void setKategorije(ArrayList<Kategorija> kategorije) {
        this.kategorije = kategorije;
    }

    public void setRequestedKategorija(Kategorija requestedKategorija) {
        this.requestedKategorija = requestedKategorija;
    }

    public void setMogucaPitanja(ArrayList<Pitanje> mogucaPitanja) {
        this.mogucaPitanja = mogucaPitanja;
    }

    public ArrayList<String> getPitanjaIDMask() {
        return pitanjaIDMask;
    }


    public ArrayList<Kviz> getKvizovi() {
        return kvizovi;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Kviz> fetchKvizoveBaze(JSONArray jsonArray) {

        int total = jsonArray.length();

        try {
            for (int i = 0; i < total; i++) {


                JSONObject name = jsonArray.getJSONObject(i);
                JSONObject document = name.getJSONObject("document");
                JSONObject kviz = document.getJSONObject("fields");

                String naziv = kviz.getJSONObject("naziv").getString("stringValue");
                String idKategorije = kviz.getJSONObject("idKategorije").getString("stringValue");
                if (naziv.equals("")) continue;

                JSONArray items = kviz.getJSONObject("pitanja").getJSONObject("arrayValue").getJSONArray("values");  //lista pitanja kviza

                for (Kategorija k : kategorije) {                   // imamo li kategoriju vec
                    if (k.getNaziv().equals(idKategorije)) {
                        requestedKategorija = k;
                    }
                }

                ArrayList<Pitanje> pitanja = new ArrayList<>();

                for (int j = 0; j < items.length(); j++) {
                    //provjeriti id moze li
                    String nazivPitanja = items.getJSONObject(j).getString("stringValue");

                    if (!pitanjaIDMask.contains(nazivPitanja))
                        pitanjaIDMask.add(nazivPitanja);

                    for (Pitanje trenutno : mogucaPitanja) {
                        if (trenutno.getNaziv().equals(nazivPitanja)) {
                            pitanja.add(trenutno);
                        }
                    }

                }
                boolean imamoUListi = false;

                //Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija)
                for (Kviz temp : kvizovi) {
                    if (temp.getNaziv().equals(naziv)) {
                        imamoUListi = true;
                    }
                }
                if (!imamoUListi) {
                    kvizovi.add(new Kviz(naziv, pitanja, requestedKategorija));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pitanjaIDMask = new ArrayList<>();
        return kvizovi;
    }

    @Override
    protected Void doInBackground(String... strings) {


        upit = "{\n" +
                "    \"structuredQuery\": {\n";
        if (!idKategorije.equals("Svi")) {
            upit += "    \"where\" : {\n" +
                    "            \"fieldFilter\" : { \n" +
                    "                \"field\": {\"fieldPath\": \"idKategorije\"}, \n" +
                    "                \"op\":\"EQUAL\", \n" +
                    "                \"value\": {\"stringValue\": \"" + idKategorije + "\"}\n" +
                    "            }\n" +
                    "        },\n";
        }
        upit += " \"select\": { \"fields\": [ {\"fieldPath\": \"idKategorije\"}, {\"fieldPath\": \"naziv\"}, {\"fieldPath\": \"pitanja\"}] },\n" +
                "        \"from\": [{\"collectionId\": \"Kvizovi\"}],\n" +
                "       \"limit\": 1000 \n" +
                "    }\n" +
                "}";

        urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/(default)/documents:runQuery?access_token="
                + token;

        try {
            // otvaramo konekciju
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setDoOutput(true);
            request.setRequestMethod("POST");

            request.setRequestProperty("Content-Type", "application/json; utf-8");
            request.setRequestProperty("Accept", "application/json");

            String rezultat;
            if (idKategorije.equals("Svi")) {
                try (OutputStream os = request.getOutputStream()) {
                    byte[] input = upit.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                InputStream in = request.getInputStream();
                rezultat = "{\"documents\": " + (FetchPitanjaBaza.streamToStringConvertor(in)) + "}";
                JSONObject jsonObject = new JSONObject(rezultat);
                JSONArray items = jsonObject.getJSONArray("documents");
                kvizovi = fetchKvizoveBaze(items);

            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
