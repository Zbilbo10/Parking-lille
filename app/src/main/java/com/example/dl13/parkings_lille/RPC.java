package com.example.dl13.parkings_lille;


import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;



public class RPC  extends AsyncTask<Void, Void, String> {

    private volatile MainActivity main;
    private HttpsURLConnection client;
    private String BASE_URL = "https://opendata.lillemetropole.fr/api/records/1.0/search/?";

    public RPC(MainActivity a)
    {
        this.main = a;
    }


    @Override
    protected String doInBackground(Void... Params){

        String json = null;

        try
        {
            URL url = new URL(BASE_URL + "dataset=disponibilite-parkings&facet=libelle&facet=ville&facet=etat&rows=24");

            client = (HttpsURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(client.getInputStream());

            json = convert(in);

        }
        catch(Exception e)
        {
            Log.e("Error : ", e.getMessage());
        }
        finally
        {
            client.disconnect();
            return json;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        this.main.populate(result);
    }


    private String convert(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
