package com.example.dl13.parkings_lille;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.HashMap;
import im.delight.android.location.SimpleLocation;


public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, String> p;
    private SimpleLocation location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        p = (HashMap<String, String>) getIntent().getSerializableExtra("parking");

        TextView Nom = (TextView)findViewById(R.id.Nom);
        TextView adresse = (TextView)findViewById(R.id.adresse);
        TextView distance = (TextView)findViewById(R.id.distance);
        TextView places = (TextView)findViewById(R.id.places);


        Nom.setText(p.get("libelle") + " / " + p.get("etat"));
        adresse.setText("Adresse : " + p.get("adresse") + ", " + p.get("ville"));

        int dist;
        double km;

        if(!p.get("distance").equals("Indisponible")) {
            dist = Integer.parseInt(p.get("distance"));
            km = dist/(1000.00);

            if (dist < 1) distance.setText("Distance : " + p.get("distance") + "m");
            else distance.setText("Distance : " + km+"" + " km");
        }
        else distance.setText("Distance : Indisponible");

        places.setText("Nombre Total de places : " + p.get("max"));

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        /**
         *  Creation des points LatLng de l'utilisateur et du parking cible
         */
        LatLng parking = new LatLng(Double.parseDouble(p.get("latitude")),Double.parseDouble(p.get("longitude")));
        LatLng moi = new LatLng(location.getLatitude(), location.getLongitude());


        /**
         *  Ajout du marqueurs du parkings et activation de l'option de marquage de la position de l'utilisateur
         */
        mMap.addMarker(new MarkerOptions().position(parking));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }


        /**
         *  Paramétrage de la caméra pour que les deux points apparaissent à l'écran
         */
        LatLngBounds bounds = setLagLngBounds(moi, parking);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,width, height, 200));

        /**
         * Ajout de l'itinéraire
         */
        GestionItineraire();

    }

    /**
     *  Création d'une bordure en fonction du positionnement des deux points de référence
     * @param moi: position actuelle de l'utilisateur
     * @param p: position du parking cible
     * @return
     */
    private LatLngBounds setLagLngBounds(LatLng moi, LatLng p) {

        LatLngBounds bounds = null;

        if(moi.latitude > p.latitude && moi.longitude > p.longitude) bounds = new LatLngBounds(p, moi);
        else if (moi.latitude < p.latitude && moi.longitude < p.longitude) bounds = new LatLngBounds(moi, p);
        else if (moi.latitude < p.latitude && moi.longitude > p.longitude) bounds = new LatLngBounds(new LatLng(moi.latitude, p.longitude), new LatLng(p.latitude, moi.longitude));
        else if (moi.latitude > p.latitude && moi.longitude < p.longitude) bounds = new LatLngBounds(new LatLng(p.latitude, moi.longitude), new LatLng(moi.latitude, p.longitude));

        return bounds;
    }

    /**
     * Ajout de l'itinéraire vers le parking cible
     */
    private void GestionItineraire()
    {
        ItineraireRPC RPC = new ItineraireRPC(this, p.get("latitude"), p.get("longitude"), location);

        try {
            String result = RPC.execute().get();


            if (!result.equals("null")) {
                //transformation @result en arrayList
                String[] donnees = result.split(";");
                ArrayList<LatLng> lstLatLng = new ArrayList<>();

                for (int i = 0; i < donnees.length; i++) {
                    lstLatLng.add(new LatLng(Double.parseDouble(donnees[i].split(",")[0]), Double.parseDouble(donnees[i].split(",")[1])));
                }

                // Création polyline
                final PolylineOptions polylines = new PolylineOptions();
                polylines.color(Color.BLUE);

                //On construit le polyline
                for (final LatLng latLng : lstLatLng) {
                    polylines.add(latLng);
                }

                //On ajoute le polyline à la map
                mMap.addPolyline(polylines);
            }
        }
        catch (Exception e)
        {
            Log.e("Erreur itineraire", e.getMessage());
        }
    }
}