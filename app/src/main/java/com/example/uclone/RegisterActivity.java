package com.example.uclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    final static String TAG = "REGISTER";
    final static int IMAGE_CHOOSE_INTENT = 1;
    Task location;
    Geocoder geocoder;
    CardView patiantcarview;
            private Double lat,lon;
    int PERMISSION_ID = 44;
    LinearLayout drlinearlayout;
    Location currentLocation;
    List<Address> addresses;
    private EditText address_textview;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button mBtnRegister;
    private Spinner gender;
    private EditText mEmail, mPassword, mConfrimPassword, mContact, mName,cnic,age,specialization;
    RadioButton previousyes,previousno,pressureyes,pressureno,assocyes,assocno;
    private FirebaseAuth mAuth;
    private ImageView mProfileImage,tile_img;
    private Uri resultUri;
    private String getintent,getprevioushistory,pressurecheck,associancheck;
    private HelpingMethods helpingMethods;
    private ProgressDialog progressDialog;
    Boolean preferencesCheck = false;
    PreferencesClass preferencesClass;
    DatabaseReference mCustomerReference;
    private DatabaseReference mCustomerDatabaseReference;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            preferencesClass.setUserPassword(mPassword.getText().toString());
            preferencesClass.saveUser(dataSnapshot,getintent);
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            Toast.makeText(RegisterActivity.this, "User Created Successfully!", Toast.LENGTH_LONG).show();
            finish();

            startActivity(intent);
            progressDialog.cancel();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getintent  = getIntent().getStringExtra("userfor");
        mName = findViewById(R.id.name);
        cnic = findViewById(R.id.cnic);
        age = findViewById(R.id.Age);
        assocyes = findViewById(R.id.assocyes);
        assocno = findViewById(R.id.assocno);
        drlinearlayout = findViewById(R.id.drlinearlayout);
        patiantcarview = findViewById(R.id.patiantcarview);
        previousyes = findViewById(R.id.previousyes);
        specialization = findViewById(R.id.specialization);
        previousno = findViewById(R.id.previousno);
        pressureyes = findViewById(R.id.pressureyes);
        pressureno= findViewById(R.id.pressureno);
        mEmail = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        String[] floorList = new String[]{"Select Gender","Male","Female"};
        ArrayAdapter<String> floorArray = new ArrayAdapter<String>(RegisterActivity.this, R.layout.support_simple_spinner_dropdown_item, floorList);
        floorArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(floorArray);

        previousyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    getprevioushistory = "yes";
                    if (previousno.isChecked()){
                        previousno.setChecked(false);
                    }
                    Toast.makeText(RegisterActivity.this, ""+getprevioushistory, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e ){
                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        assocyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    associancheck = "yes";
                    if (assocno.isChecked()){
                        previousno.setChecked(false);
                    }
                    Toast.makeText(RegisterActivity.this, ""+getprevioushistory, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e ){
                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        previousno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getprevioushistory = "no";
                previousyes.setChecked(false);
                Toast.makeText(RegisterActivity.this, ""+getprevioushistory, Toast.LENGTH_SHORT).show();
            }
        });
        assocno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                associancheck = "no";
                previousyes.setChecked(false);
                Toast.makeText(RegisterActivity.this, ""+getprevioushistory, Toast.LENGTH_SHORT).show();
            }
        });
        pressureyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    pressurecheck = "yes";
                    if (pressureno.isChecked()){
                        pressureno.setChecked(false);
                    }
                    Toast.makeText(RegisterActivity.this, ""+getprevioushistory, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e ){
                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        pressureno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressurecheck = "no";
                pressureyes.setChecked(false);
                Toast.makeText(RegisterActivity.this, ""+getprevioushistory, Toast.LENGTH_SHORT).show();
            }
        });

        mPassword = findViewById(R.id.password);
        mConfrimPassword = findViewById(R.id.confirm_password);
        mContact = findViewById(R.id.contact);
        mBtnRegister = findViewById(R.id.btn_register);
        helpingMethods = new HelpingMethods(this);
        preferencesClass = new PreferencesClass(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();
        tile_img = findViewById(R.id.tile_img);
        address_textview = findViewById(R.id.select_spinner);

        if(getintent.equals("Patient")){
            patiantcarview.setVisibility(View.VISIBLE);

        }
        else if (getintent.equals("Doctor")){
            drlinearlayout.setVisibility(View.VISIBLE);
        }
        Dexter.withActivity(RegisterActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        Intent ll = new Intent(RegisterActivity.this, PostSearch_Activity.class);
//                        ll.putExtra("for", "nearby");
//                        startActivity(ll);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(RegisterActivity.this);

                        getLastLocation();

//                        location.addOnCompleteListener(new OnCompleteListener() {
//                            @Override
//                            public void onComplete(@NonNull Task task) {
//                                if (task.isSuccessful()) {
//
//
//                                    currentLocation = (Location) task.getResult();
//                                    geocoder = new Geocoder(RegisterActivity.this);
//                                    try {
//                                        String  address = null,city;
//
//
//                                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(),1);
//
//                                        address = addresses.get(0).getAddressLine(0);
//                                        city = addresses.get(0).getLocality();
//                                        address_textview.setText(address+" "+city);
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Glide.with(getApplicationContext()).load("https://maps.googleapis.com/maps/api/staticmap?center=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&zoom=15&size=640x450&maptype=roadmap&markers=color:red%7Clabel:Property%7C" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "" + "&key=AIzaSyAAdMS03mAk6qDSf4HUmZmcjvSkiSN7jIU").into(tile_img);
//
//                                } else {
//                                    Log.d("Erroraya", String.valueOf(task.getException()));
//                                }
//                            }
//                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        }
                                    })
                                    .show();
                        }

                        else {
                            Toast.makeText(RegisterActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();




        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {

                    mCustomerDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getintent).child(user.getUid());


                    String name = mName.getText().toString().trim();
                    String email = mEmail.getText().toString().trim();
                    String contact = mContact.getText().toString().trim();
                    HashMap hashMap = new HashMap();
                    hashMap.put("name", name);
                    hashMap.put("email", email);
                    hashMap.put("contact", contact);
                    hashMap.put("cnic", cnic.getText().toString());
                    hashMap.put("address", address_textview.getText().toString());
                    hashMap.put("latlng", lat+","+lon);

                    if (getintent.equals("Patient")){
                        hashMap.put("age", age.getText().toString());
                        hashMap.put("getprevioushistory", getprevioushistory);
                        hashMap.put("pressurecheck", pressurecheck);
                        hashMap.put("status","1");
                    }
                    else if (getintent.equals("Doctor")){
                        //umair
                        hashMap.put("gender", gender.getSelectedItem().toString());
                        hashMap.put("association", associancheck);
                        hashMap.put("specialization",specialization.getText().toString());
                        hashMap.put("status","1");
                    }
                    else{
                        hashMap.put("status","1");
                    }

                    try{
                        mCustomerDatabaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (resultUri != null) {
                                        uploadImage();
                                    } else {
                                        if (preferencesClass.getUSER_NAME() == null) {
                                            progressDialog.show();

                                            savePreferences();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "User Created Successfully!", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    catch (Exception e){
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        mProfileImage = findViewById(R.id.profile_image);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_CHOOSE_INTENT);
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = helpingMethods.createProgressDialog("Registering.", "Please Wait");
                progressDialog.show();
                if (validateRegisterData()) {
                    final String email = mEmail.getText().toString().trim();
                    final String password = mPassword.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this,"User Created",Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(RegisterActivity.this, "Register Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            final Location mLastLocation = locationResult.getLastLocation();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    getlocationlatlng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    return null;
                }
            };

        }
    };
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull
                                                           Task<Location> task) {
                                final Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {


                                        getlocationlatlng(location.getLatitude(),location.getLongitude());
//                                    new AsyncTask<Void, Void, Void>() {
//                                        @Override
//                                        protected Void doInBackground(Void... voids) {
//
//                                            return null;
//                                        }
//                                    };
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location",
                        Toast.LENGTH_LONG).show();
                Intent intent = new
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new
                        String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private void getlocationlatlng(double latitude, double longitude) {
    lat = latitude;
    lon   = longitude;
        try {
            geocoder = new Geocoder(RegisterActivity.this);
            addresses = geocoder.getFromLocation(latitude, longitude,1);
            String  address = null,city;
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            address_textview.setText(address+" "+city);
            Glide.with(getApplicationContext()).load("https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=640x450&maptype=roadmap&markers=color:red%7Clabel:Property%7C" + latitude + "," + longitude + "" + "&key=AIzaSyAAdMS03mAk6qDSf4HUmZmcjvSkiSN7jIU").into(tile_img);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    private boolean validateRegisterData() {

        boolean flag = true;
        String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfrimPassword.getText().toString().trim();
        String contact = mContact.getText().toString().trim();


        if (name.isEmpty()) {
            mName.setError("Name Required");
            flag = false;
        }
        if (email.isEmpty()) {
            mEmail.setError("Email Required");
            flag = false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
                mEmail.setError("Invalid Email");
                flag = false;
            }
        }
        if (password.isEmpty()) {
            mPassword.setError("Password Required");
            flag = false;
        }
        if (confirmPassword.isEmpty()) {
            mConfrimPassword.setError("Password Required");
            flag = false;
        }
        if ((!confirmPassword.isEmpty()) && (!password.isEmpty())) {
            if (!confirmPassword.equals(password)) {
                mConfrimPassword.setError("Password Mismatch");
                flag = false;
            }
        }
        if (contact.isEmpty()) {
            mContact.setError("Contact Required");
            flag = false;
        } else {
            Pattern pattern = Pattern.compile("^((\\+923)\\d{9})$");
            if (!pattern.matcher(contact).matches()) {
                mConfrimPassword.setError("Invalid Fomrmat");
                flag = false;
            }
        }
        return flag;
    }

    private void uploadImage() {
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(mAuth.getUid());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = filePath.putBytes(data);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.v(TAG, task.getException().getMessage());
                    progressDialog.dismiss();
                }


                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUri.toString());
                    mCustomerDatabaseReference.updateChildren(newImage);
                    preferencesClass.setUserImage(downloadUri.toString());
                    savePreferences();
                } else {
                    Log.v(TAG, task.getException().getMessage());
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CHOOSE_INTENT && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    private void savePreferences() {
        mCustomerReference = helpingMethods.getCustomerReference(getintent);
        preferencesCheck = true;
        mCustomerReference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
        if (preferencesCheck)
            mCustomerReference.removeEventListener(valueEventListener);
    }
}
