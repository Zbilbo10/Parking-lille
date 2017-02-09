package com.example.dl13.parkings_lille;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import im.delight.android.location.SimpleLocation;


public class DistanceRPC extends AsyncTask<Void, Void, String> {

    private volatile MainActivity main;
    private HttpsURLConnection client;
    private String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private String latitude;
    private String longitude;
    private SimpleLocation location;

    public DistanceRPC(MainActivity a, String latitude, String longitude, SimpleLocation location)
    {
        this.location = location;
        this.main = a;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected String doInBackground(Void... Params){

        String distance = null;

        try
        {
            URL url = new URL(BASE_URL + "origin=" + String.valueOf(location.getLatitude()) + "," +  String.valueOf(location.getLongitude()) + "&destination=" + latitude + "," + longitude + "&key=AIzaSyANgBF2Kbg2rXZVycfFDgY8TCuoGOlAWAQ");


            client = (HttpsURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            client.getResponseMessage();
            InputStream in = new BufferedInputStream(client.getInputStream());

             String json = convert(in);

            // Positionnement sur le bon noeud
            JSONObject obj = new JSONObject(json);
            obj = obj.getJSONArray("routes").getJSONObject(0);
            obj = obj.getJSONArray("legs").getJSONObject(0);

            // Récupération distance
            distance = obj.getJSONObject("distance").getString("value");
        }
        catch(Exception e)
        {
            Log.e("Error A : ", e.getMessage());
            distance = "Indisponible";
        }
        finally
        {
            client.disconnect();
        }

        return distance;
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
