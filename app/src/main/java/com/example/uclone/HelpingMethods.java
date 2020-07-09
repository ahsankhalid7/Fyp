package com.example.uclone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HelpingMethods {

    public final static int MSG_SUCCESS = 1;
    public final static int MSG_ERROR = 0;


    private Activity activity;

    public HelpingMethods(Activity activity) {
        this.activity = activity;
    }

    public void snackbarMessage(String msg, View v, int msg_code) {
        Snackbar sb = Snackbar.make(v, msg, Snackbar.LENGTH_SHORT);
        View sbView = sb.getView();
        if(msg_code == MSG_SUCCESS)
            sbView.setBackgroundColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        else
            sbView.setBackgroundColor(ContextCompat.getColor(activity,R.color.error));

        sb.show();
    }

    public ProgressDialog createProgressDialog(String title, String message) {
        ProgressDialog mDialog = new ProgressDialog(activity);
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        return mDialog;
    }

    public DatabaseReference getCustomerReference(String userfor) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return FirebaseDatabase.getInstance().getReference().child("Users").child(userfor).child(mAuth.getUid());
    }
}
