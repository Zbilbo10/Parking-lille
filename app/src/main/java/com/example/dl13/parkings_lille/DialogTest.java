package com.example.dl13.parkings_lille;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;



public class DialogTest extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Autorisation")
                .setMessage("Autorisation nécessaire pour envoyer un sms")
                .setPositiveButton("Autoriser", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        askForPermission();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    @TargetApi(23)
    private void askForPermission() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);

    }
}