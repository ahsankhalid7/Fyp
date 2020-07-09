package com.example.uclone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afraz on 7/23/2018.
 */

public class PreferencesClass {

    private Activity activity;
    public static String TAG = "PREFERENCES";
    public PreferencesClass(Activity activity) {
        this.activity = activity;
    }

    public void saveUser(DataSnapshot dataSnapshot,String userfor){

        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.USER_NAME,dataSnapshot.child("name").getValue().toString());
        editor.putString(AppConstants.USER_EMAIL,dataSnapshot.child("email").getValue().toString());
        editor.putString(AppConstants.USER_CONTACT,dataSnapshot.child("contact").getValue().toString());
        editor.putString(AppConstants.USER_IMAGE,dataSnapshot.child("profileImageUrl").getValue().toString());
        editor.putString(AppConstants.Address,dataSnapshot.child("address").getValue().toString());
        editor.putString(AppConstants.latlng,dataSnapshot.child("latlng").getValue().toString());
        editor.putString(AppConstants.USER_TYPE,dataSnapshot.child("address").getValue().toString());
        editor.putString(AppConstants.USER_FOR,userfor);
        editor.apply();
}

public void setUserImage(String image){
    SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(AppConstants.USER_IMAGE,image);
    editor.apply();

    Log.d(TAG,"IMAGE: "+image, null);

}
    public void setUserPassword(String password){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.USER_PASSWORD,password);
        editor.apply();

    }


    public void setUserContact(String contact){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.USER_CONTACT,contact);
        editor.apply();

    }

    public void setArrivedNot(String check){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.ARRIVED_NOT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.ARRIVED_NOT,check);
        editor.apply();

    }


    public String getUSER_NAME() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString(AppConstants.USER_NAME, null);
        Log.d(TAG,"NAME: "+name);
        return name;
    }
    public String getaddress() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString(AppConstants.Address, null);
        Log.d(TAG,"NAME: "+name);
        return name;
    }
    public String getaddress2() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString(AppConstants.USER_TYPE, null);
        Log.d(TAG,"NAME: "+name);
        return name;
    }
    public String getlatng() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString(AppConstants.latlng, null);
        Log.d(TAG,"NAME: "+name);
        return name;
    }
    public String get_USERFOR() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString(AppConstants.USER_FOR, null);
        Log.d(TAG,"NAME: "+name);
        return name;
    }



    public String getUSER_EMAIL() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(AppConstants.USER_EMAIL, null);
        Log.d(TAG,"EMAIL: "+email);
        return email;
    }
    public String getUSER_Password() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String password = sharedPreferences.getString(AppConstants.USER_PASSWORD, null);
        Log.d(TAG,"PASWORD: "+password);
        return password;
    }


    public  String getUserContact() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String contact =  sharedPreferences.getString(AppConstants.USER_CONTACT, null);
        Log.d(TAG,"CONTACT: "+contact);
        return contact;
    }


    public  String getUserImage() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.USER_IMAGE, null);
    }

    public  String getArrivedNot() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.ARRIVED_NOT, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.ARRIVED_NOT, "false");
    }


    public void clearUser(){

        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        String imageString =  Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        return imageString;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void saveautostart(String uid){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();

    }


}