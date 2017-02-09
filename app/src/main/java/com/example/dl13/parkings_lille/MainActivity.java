package com.example.dl13.parkings_lille;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import im.delight.android.location.SimpleLocation;


public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<HashMap<String, String>> parkings;
    private ProgressDialog progress;
    private SimpleLocation location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = new SimpleLocation(this);

        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        lv = (ListView) findViewById(R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("parking", parkings.get(position));
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("parkings", parkings);
                startActivity(intent);
            }
        });

        this.progress = new ProgressDialog(this);

        parkings = new ArrayList<>();
    }

    protected void onStart() {
        super.onStart();

        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                progress.setTitle("Veuillez patienter");
                progress.setMessage("Récupération des données en cours...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();

                new RPC(this).execute();
        }
        else
        {
            demandeAutorisation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }


    public void populate(String data) {
        gestionJson(data);

        Collections.sort(parkings, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {

                if (o1.get("etat").equals(o2.get("etat"))) {
                    return o1.get("distance").compareTo(o2.get("distance"));
                } else {
                    return o2.get("etat").compareTo(o1.get("etat"));
                }
            }
        });

        ListAdapter adapter = new ParkingAdapter(this, parkings);
        lv.setAdapter(adapter);

        if (progress.isShowing()) progress.dismiss();
    }

    private void gestionJson(String data) {

        if (data != null) {
            try {
                JSONObject jsonObj = new JSONObject(data);

                JSONArray records = jsonObj.getJSONArray("records");

                parkings.clear();

                for (int i = 0; i < records.length(); i++) {
                    JSONObject p = records.getJSONObject(i);
                    JSONObject fields = p.getJSONObject("fields");
                    JSONArray coords = fields.getJSONArray("coordgeo");

                    DistanceRPC RPC = new DistanceRPC(this, coords.getString(0), coords.getString(1), location);
                    String result = RPC.execute().get();

                    String etat;
                    String dispo;

                    try {
                        etat = fields.getString("etat");
                    } catch (Exception e) {
                        etat = "OUVERT";
                    }

                    try {
                        dispo = fields.getString("dispo");
                    } catch (Exception e) {
                        dispo = "Indisponible";
                    }

                    if (dispo.equals(0 + "")) etat = "COMPLET";
                    if (etat.equals("ABONNES")) etat = "FERME";
                    if (etat.equals("LIBRE")) etat = "OUVERT";

                    HashMap<String, String> parking = new HashMap<>();

                    parking.put("ville", fields.getString("ville"));
                    parking.put("libelle", fields.getString("libelle"));
                    parking.put("adresse", fields.getString("adresse"));
                    parking.put("etat", etat);
                    parking.put("dispo", dispo);
                    parking.put("max", fields.getString("max"));
                    parking.put("latitude", coords.getString(0));
                    parking.put("longitude", coords.getString(1));
                    parking.put("distance", result);

                    parkings.add(parking);
                }
            } catch (Exception e) {
                Log.e("Error : ", e.getMessage());
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Connexion internet impossible. \n" + "Veuillez réessayer ultérieurement.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }


    /**
     * Gestion des permissions (ici ACCESS_FINE_LOCATION)
     */
    @TargetApi(23)
    private void askForPermission()
    {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }

    @TargetApi(23)
    public void demandeAutorisation()
    {
        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            dialogTest();
        }
        else
        {
            askForPermission();
        }
    }

    public void dialogTest(){
        DialogFragment newfrag = new DialogTest();
        newfrag.show(getFragmentManager(), "test");
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 2)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                onStart();
            }
            else if(!shouldShowRequestPermissionRationale(permissions[0]))
            {
                displayOptions();
            }
            else
            {
                dialogTest();
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void displayOptions()
    {
        DialogFragment newfrag = new DialogParam();
        newfrag.show(getFragmentManager(), "Paramètres");
    }
}
