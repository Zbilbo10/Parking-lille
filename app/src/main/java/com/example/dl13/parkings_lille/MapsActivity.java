package com.example.dl13.parkings_lille;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<HashMap<String, String>> parkings;
    private final ArrayList<Marker> lstMarker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        parkings = (ArrayList<HashMap<String, String>>)intent.getSerializableExtra("parkings");

        /*if(!isNetworkAvailable())
        {
            Toast.makeText(getApplicationContext(),
                    "Connexion internet impossible. \n" + "Veuillez réessayer ultérieurement.",
                    Toast.LENGTH_LONG)
                    .show();
        }*/
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        for (final HashMap<String, String> parking : parkings)
        {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(parking.get("latitude")), Double.parseDouble(parking.get("longitude")))).title(parking.get("name")));
            lstMarker.add(marker);
        }

        LatLng lille = new LatLng(50.63, 3.06);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lille, 13));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try {
                    int indice = lstMarker.indexOf(marker);

                    HashMap<String, String> p = parkings.get(indice);

                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    intent.putExtra("parking", p);
                    startActivity(intent);

                }
                catch (Exception e)
                {
                }
                return false;
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
