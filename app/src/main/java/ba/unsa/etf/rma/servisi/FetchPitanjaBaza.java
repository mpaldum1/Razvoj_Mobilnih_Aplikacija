package ba.unsa.etf.rma.servisi;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class FetchPitanjaBaza extends AsyncTask<String, Integer, ArrayList<Pitanje>> {

    private String token;
    private String nazivKolekcije;
    final private String projectID = "rmaspirala-1bc9b";
    private String method;
    private Kviz kviz;

    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private ArrayList<String> odgovori = new ArrayList<>();
    private ArrayList<Pitanje> mogucaPitanja = new ArrayList<>();

    public ArrayList<Pitanje> getMogucaPitanja() {
        return mogucaPitanja;
    }

    public void setNazivKolekcije(String nazivKolekcije) {
        this.nazivKolekcije = nazivKolekcije;
    }

    public   ArrayList<Pitanje> fetchPitanjaBaze(JSONArray items) throws JSONException {              // ucitavamo pitanja iz baze
        int length = items.length();
        for (int i = 0; i < length; i++) {

            JSONObject trenutniObjekat = items.getJSONObject(i);
            String id = trenutniObjekat.getString("name");
            JSONObject trenutniKviz = trenutniObjekat.getJSONObject("fields");
            String naziv = trenutniKviz.getJSONObject("naziv").getString("stringValue");
            int index = trenutniKviz.getJSONObject("indexTacnog").getInt("integerValue");
            JSONArray jsonArray = trenutniKviz.getJSONObject("odgovori").getJSONObject("arrayValue").getJSONArray("values");
            int arrayLength = jsonArray.length();

            for (int j = 0; j < arrayLength; j++) {
                odgovori.add(jsonArray.getJSONObject(j).getString("stringValue"));
            }

            //   public Pitanje(String naziv, String tekstPitanja, String tacan, ArrayList<String> odgovori)
            Pitanje pitanje = new Pitanje(naziv, naziv, odgovori.get(index), odgovori);
            pitanja.add(pitanje);

        }
        return pitanja;
    }


    @Override
    protected ArrayList<Pitanje> doInBackground(String... strings) {

        String urlString = "https://firestore.googleapis.com/v1/projects/" + "/databases/(default)/documents/"
                + nazivKolekcije + "?access_token=" + token;

        URL url = null;                                                       // postavljamo konekciju

        try {
            url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            InputStream inputStream = new BufferedInputStream(request.getInputStream());
            String converted = streamToStringConvertor(inputStream);
            JSONObject jsonObject = new JSONObject(converted);
            JSONArray jsonArray;

            if (nazivKolekcije.equals("Pitanja")) {
                jsonArray = jsonObject.getJSONArray("documents");
                pitanja = fetchPitanjaBaze(jsonArray);
                for (Pitanje trenutno : pitanja) {
                    if (!kviz.getPitanja().contains(trenutno)) {
                        pitanja.add(trenutno);
                    }
                    mogucaPitanja.add(trenutno);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pitanja;
    }

        public static String streamToStringConvertor(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp + "\n");
        }
        inputStream.close();

        return stringBuilder.toString();
    }


}
