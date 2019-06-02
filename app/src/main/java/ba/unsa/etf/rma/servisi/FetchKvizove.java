package ba.unsa.etf.rma.servisi;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kviz;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.token;
import static ba.unsa.etf.rma.servisi.FetchPitanjaBaza.streamToStringConvertor;

public class FetchKvizove extends AsyncTask<String, Void, Void> {

    private String urlString = "";
    private final String projectID = "rmaspirala-1bc9b";
    private String upit = "";
    private String idKategorije = "";
    private ArrayList<Kviz> kvizovi = new ArrayList<>();

    private ArrayList<Kviz> fetchKvizoveBaze(JSONArray jsonArray) {

        ArrayList<Kviz> kvizoviIzBaze = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject name = jsonArray.getJSONObject(i);

                JSONObject dokument = name.getJSONObject("document");
                JSONObject kviz = dokument.getJSONObject("fields");

                String naziv = kviz.getJSONObject("naziv").getString("stringValue");
                String idKategorije = kviz.getJSONObject("idKategorije").getString("stringValue");
                ArrayList<String> idPitanja = new ArrayList<String>();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kvizoviIzBaze;
    }

    @Override
    protected Void doInBackground(String... strings) {

        upit = "{\n" +
                "   \"structuredQuery\": {\n" +
                "   \"where\": {\n" +
                "   \"fieldFilter\": {\n" +
                "   \"field\": {\n" +
                "   \fieldPath\": \"idKategorije\"\n" +
                "   }\n" +
                "   \"op\": \"EQUAL\",\n" +
                "   \"value\":{\n" +
                "   \"stringValue\": \"" + idKategorije + "\"\n" +
                "    }\n" +
                "    }\n" +
                "    },\n" +
                "    \"from\": [\n" +
                "    {\n" +
                "    \"collectionId\": \"Kvizovi\"\n" +
                "    }\n" +
                "    ]\n" +
                "    }\n" +
                "    }";


        urlString = "https://firestore.googleapis.com/v1/projects/" + projectID +
                "/databases/(default)/documents:runQuery?access_token=" + token;

        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setDoOutput(true);
            request.setRequestMethod("POST");


            request.setRequestProperty("Content-Type", "application/json; utf-8");
            request.setRequestProperty("Accept", "application/json");

            try (OutputStream os = request.getOutputStream()) {
                byte[] input = upit.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            InputStream inputStream = request.getInputStream();
            String result = "{ \"documents\": " + streamToStringConvertor(inputStream) + "}";
            ;
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("documents");
            kvizovi = fetchKvizoveBaze(jsonArray);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(request.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
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
