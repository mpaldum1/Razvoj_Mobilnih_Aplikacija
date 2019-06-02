package ba.unsa.etf.rma.servisi;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class FetchKategorijeBaza extends AsyncTask<String, Integer, Void> {

    private Context context;
    private GoogleCredential credential;

    private URL url;
    private ArrayList<Kategorija> kategorije = new ArrayList<>();

    public FetchKategorijeBaza(Context context) {
        this.context = context;
    }

    private ArrayList<Kategorija> fetchKategorijeBaze(JSONArray jsonArray) throws JSONException {
        ArrayList<Kategorija> listaKategorija = new ArrayList<>();
        int total = jsonArray.length();
        for (int i = 0; i < total; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("name");
            JSONObject trenutniKviz = jsonObject.getJSONObject("fields");
            String ime = trenutniKviz.getJSONObject("naziv").getString("stringValue");
            String idIkonice = trenutniKviz.getJSONObject("idIkonice").getString("integerValue");

            // Kategorija(String naziv, String id)
            listaKategorija.add(new Kategorija(ime, idIkonice));
        }
        return listaKategorija;
    }

    @Override
    protected Void doInBackground(String... strings) {

        InputStream is = context.getResources().openRawResource(R.raw.secret);
        try {
            credential = GoogleCredential.fromStream(is).createScoped(
                    Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            credential.refreshToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String token = credential.getAccessToken();


        String urlString = "https://firestore.googleapis.com/v1/projects/" + "/databases/(default)/documents/Kategorije?access_token=" + token;


        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            InputStream inputStream = new BufferedInputStream(request.getInputStream());
            String converted = FetchPitanjaBaza.streamToStringConvertor(inputStream);
            JSONObject jsonObject = new JSONObject(converted);
            JSONArray jsonArray = jsonObject.getJSONArray("documents");
            kategorije = fetchKategorijeBaze(jsonArray);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
