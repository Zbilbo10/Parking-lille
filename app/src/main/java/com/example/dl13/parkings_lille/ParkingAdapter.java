package com.example.dl13.parkings_lille;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;



public class ParkingAdapter extends BaseAdapter {

    private List<HashMap<String, String>> parkings;
    private Context mContext;
    private LayoutInflater mInflater;


    public ParkingAdapter(Context context, List<HashMap<String, String>> aListP) {
        mContext = context;
        parkings = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return parkings.size();
    }

    public Object getItem(int position) {
        return parkings.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;

        if (convertView == null) {

            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.list_item, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView name = (TextView) layoutItem.findViewById(R.id.name);
        TextView dispo = (TextView) layoutItem.findViewById(R.id.dispo);
        TextView distance = (TextView) layoutItem.findViewById(R.id.distance);
        TextView places = (TextView) layoutItem.findViewById(R.id.places);

        int dist;
        double km;

        if(!parkings.get(position).get("distance").equals("Indisponible")) {
            dist = Integer.parseInt(parkings.get(position).get("distance"));
            km = dist/(1000.00);
            String Skm = km+"";
            if (dist < 1) distance.setText(parkings.get(position).get("distance") + "m");
            else distance.setText(Skm.replace('.', ',') + " km");
        }
        else distance.setText("Distance indisponible");

        name.setText(parkings.get(position).get("ville") + " / " + parkings.get(position).get("etat"));
        dispo.setText(parkings.get(position).get("dispo"));
        places.setText(parkings.get(position).get("max"));

        if (parkings.get(position).get("etat").equals("FERME")) {
            layoutItem.setBackgroundColor(mContext.getResources().getColor(R.color.Ferme));
            ((TextView) layoutItem.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            ((TextView) layoutItem.findViewById(R.id.dispo)).setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            ((TextView) layoutItem.findViewById(R.id.distance)).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            ((TextView) layoutItem.findViewById(R.id.places)).setTextColor(mContext.getResources().getColor(R.color.max));
        }

        if (parkings.get(position).get("etat").equals("COMPLET")) {
            layoutItem.setBackgroundColor(mContext.getResources().getColor(R.color.Nondispo));
            ((TextView) layoutItem.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(R.color.NondispoText));
            ((TextView) layoutItem.findViewById(R.id.dispo)).setTextColor(mContext.getResources().getColor(R.color.NondispoText));
            ((TextView) layoutItem.findViewById(R.id.distance)).setTextColor(mContext.getResources().getColor(R.color.NondispoText));
            ((TextView) layoutItem.findViewById(R.id.places)).setTextColor(mContext.getResources().getColor(R.color.NondispoText));
            ((TextView) layoutItem.findViewById(R.id.separation_dispo_max)).setTextColor(mContext.getResources().getColor(R.color.NondispoText));
            ((TextView) layoutItem.findViewById(R.id.text_place)).setTextColor(mContext.getResources().getColor(R.color.NondispoText));
        }

        if (parkings.get(position).get("etat").equals("OUVERT") || parkings.get(position).get("etat").equals("Information non disponible")) {
            layoutItem.setBackgroundColor(Color.WHITE);
            ((TextView) layoutItem.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            ((TextView) layoutItem.findViewById(R.id.dispo)).setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            ((TextView) layoutItem.findViewById(R.id.distance)).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            ((TextView) layoutItem.findViewById(R.id.places)).setTextColor(mContext.getResources().getColor(R.color.max));
        }

        return layoutItem;
    }

}
