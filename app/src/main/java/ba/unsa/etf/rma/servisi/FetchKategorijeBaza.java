package ba.unsa.etf.rma.servisi;

import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.projectID;
import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.token;

public class FetchKategorijeBaza extends AsyncTask<String, Integer, Void> {

    private GoogleCredential credential;

    private URL url;
    private ArrayList<Kategorija> kategorije = new ArrayList<>();


    public ArrayList<Kategorija> getKategorije() {
        return kategorije;
    }

    public void fetchKategorijeBaze(JSONArray jsonArray) throws JSONException {

        ArrayList<Kategorija> helperLista = new ArrayList<>();
        int total = jsonArray.length();

        for (int i = 0; i < total; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject trenutniKviz = jsonObject.getJSONObject("fields");

            String naziv = trenutniKviz.getJSONObject("naziv").getString("stringValue");
            if (naziv.equals("")) continue;
            String idIkonice = trenutniKviz.getJSONObject("idIkonice").getString("integerValue");

            // Kategorija(String naziv, String id) - konstrukctor
            helperLista.add(new Kategorija(naziv, idIkonice));
        }

        kategorije = helperLista;
    }

    @Override
    protected Void doInBackground(String... strings) {

        String urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/(default)/documents/Kategorije?access_token=" + token;

        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            InputStream inputStream = new BufferedInputStream(request.getInputStream());
            String converted = FetchPitanjaBaza.streamToStringConvertor(inputStream);
            JSONObject jsonObject = new JSONObject(converted);
            JSONArray jsonArray = jsonObject.getJSONArray("documents");
            fetchKategorijeBaze(jsonArray);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
