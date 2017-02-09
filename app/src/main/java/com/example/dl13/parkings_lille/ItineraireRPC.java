package com.example.dl13.parkings_lille;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import im.delight.android.location.SimpleLocation;


public class ItineraireRPC extends AsyncTask<Void, Void, String> {

    private volatile DetailsActivity main;
    private HttpsURLConnection client;
    private String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private String latitude;
    private String longitude;
    private SimpleLocation location;
    private String donnees;
    private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();

    public ItineraireRPC(DetailsActivity a, String latitude, String longitude, SimpleLocation location)
    {
        this.main = a;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
    }

    @Override
    protected String doInBackground(Void... Params){

        try
        {
            String urlS = BASE_URL + "origin=" + String.valueOf(location.getLatitude()) + "," +  String.valueOf(location.getLongitude()) + "&destination=" + latitude + "," + longitude;
            //urlS += "&key=AIzaSyANgBF2Kbg2rXZVycfFDgY8TCuoGOlAWAQ"

            URL url = new URL(urlS);

            client = (HttpsURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            client.getResponseMessage();
            InputStream in = new BufferedInputStream(client.getInputStream());

            String json = convert(in);


            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("routes");
            obj = array.getJSONObject(0);
            array = obj.getJSONArray("legs");
            obj = array.getJSONObject(0);

            // Récupération données itinéraire
            array = obj.getJSONArray("steps");

            for (int i = 0 ; i < array.length() ; i++)
            {
                JSONObject step = array.getJSONObject(i);

                decodePolylines(step.getJSONObject("polyline").getString("points"));
            }

            donnees = lstLatLng.get(0).latitude+"" + "," + lstLatLng.get(0).longitude+"";

            for (int i = 1 ; i < lstLatLng.size() ; i++)
            {
                donnees += ";" + lstLatLng.get(i).latitude+"" + "," + lstLatLng.get(i).longitude+"";
            }
        }
        catch(Exception e)
        {
            Log.e("Error A : ", e.getMessage());
            donnees = "null";
        }
        finally
        {
            client.disconnect();
            return donnees;
        }
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

    private void decodePolylines(final String encodedPoints) {
        int index = 0;
        int lat = 0, lng = 0;

        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;

            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            lstLatLng.add(new LatLng((double)lat/1E5, (double)lng/1E5));
        }
    }

}
