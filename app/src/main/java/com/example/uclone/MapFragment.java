package com.example.uclone;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcel;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, RoutingListener {


    private final int LOCATION_REQUEST_CODE = 100;
    private final float DEFAULT_ZOOM = 15;
    private RecyclerView recyclerView;
    private final String TAG = "MAP FRAGMENT";

    public static List<getdoctorordriver> getdoctorordriverList;
    private double latitude,longitude;
    private View mView, mapView;
    RelativeLayout relat;
    private ImageView mLocationPin;
    private TextView mDriverName, mDriverVehicle, mDriverTime, getmDriverRating;
    private CircleImageView mDriverProfileImage;
    private GoogleMap mMap;
    private Marker mCustomerMarker;
    private Marker mHelpMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient mPlacesClient;
    private List<AutocompletePrediction> autocompletePredictionList;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private MaterialSearchBar mMaterialSearchBar;
    final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
    private RippleBackground mRippleBg;
    private Button mGo, mCancelRequest,chat;
    private BottomSheetDialog selectRideSheetDialog;
    private boolean mCarSelect = true, mBikeSelect, mRequestBol = false;
    private Routing routing;
    private List<Polyline> polylines;
    private LatLng currentMarkerLocation;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        getdoctorordriverList = new ArrayList<>();
        PreferencesClass preferencesClass = new PreferencesClass(getActivity());
        Log.i(TAG, preferencesClass.getUSER_NAME());
        Log.i(TAG, preferencesClass.getUSER_EMAIL());
        Log.i(TAG, preferencesClass.getUserContact());
        recyclerView = mView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        relat = mView.findViewById(R.id.relat);


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);



        init();

        FirebaseDatabase.getInstance().getReference().child("Users").child("Patient").child(FirebaseAuth.getInstance().getUid()).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("0")){
                    mGo.setEnabled(false);
                    mGo.setText("A request is in progress");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mView;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mMap = googleMap;
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        moveCamera(new LatLng(-34, 151),"Me");
                        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                            layoutParams.setMargins(0, 0, 40, 180);
                        }

                        //check if gps is enabled or not and then request user to enable it
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
                        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

                        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                getDeviceLocation();
                            }
                        });

                        task.addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof ResolvableApiException) {
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    try {
//                        resolvable.startResolutionForResult(getActivity(), LOCATION_REQUEST_CODE);
                                        startIntentSenderForResult(resolvable.getResolution().getIntentSender(), LOCATION_REQUEST_CODE, null, 0, 0, 0, null);
                                    } catch (IntentSender.SendIntentException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });

                        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                            @Override
                            public void onCameraMove() {

                            }
                        });

                        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                if (mMaterialSearchBar.isSuggestionsVisible())
                                    mMaterialSearchBar.clearSuggestions();
                                if (mMaterialSearchBar.isSearchEnabled())
                                    mMaterialSearchBar.disableSearch();
                                return false;
                            }
                        });


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                         //   intent.setData(Uri.fromParts("package", getActivity(), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();




    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        layoutBottomSheet = mView.findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        polylines = new ArrayList<>();
        mMaterialSearchBar = mView.findViewById(R.id.searchBar);
        mGo = mView.findViewById(R.id.btn_go);
        chat = mView.findViewById(R.id.chatbutton);


        mCancelRequest = mView.findViewById(R.id.btn_cancel_request);
        mRippleBg = mView.findViewById(R.id.ripple_bg);
        mLocationPin = mView.findViewById(R.id.location_pin);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Places.initialize(getActivity(), getString(R.string.GOOGLE_API));
        mPlacesClient = Places.createClient(getActivity());
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        mDriverName = mView.findViewById(R.id.sheet_name);
        mDriverVehicle = mView.findViewById(R.id.sheet_vehicle);
        mDriverProfileImage = mView.findViewById(R.id.sheet_profile_image);

        mMaterialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                getActivity().startSearch(text.toString(), true, null, true);

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    mMaterialSearchBar.disableSearch();
                }
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Users").child("Patient").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(getActivity(),MessagingActivity.class);
                        intent.putExtra("driverId",dataSnapshot.child("dora").getValue().toString());
                        intent.putExtra("driverName","Medical assitant");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        mMaterialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("pk")
                        .setSessionToken(token)
                        .setQuery(charSequence.toString())
                        .build();

                mPlacesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                autocompletePredictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (AutocompletePrediction autocompletePrediction : autocompletePredictionList) {
                                    suggestionsList.add(autocompletePrediction.getFullText(null).toString());
                                }

                                mMaterialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!mMaterialSearchBar.isSuggestionsVisible()) {
                                    mMaterialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i(TAG, task.getException().toString());
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mMaterialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= autocompletePredictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = autocompletePredictionList.get(position);
                String suggestion = mMaterialSearchBar.getLastSuggestions().get(position).toString();
                mMaterialSearchBar.setText(suggestion);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMaterialSearchBar.clearSuggestions();
                    }
                }, 1000);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(mMaterialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                mPlacesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());
                        LatLng latLngOfPlace = place.getLatLng();
                        if (latLngOfPlace != null) {
                            currentMarkerLocation = mMap.getCameraPosition().target;
                            if (mCustomerMarker != null)
                                mCustomerMarker = null;

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(currentMarkerLocation);
                            builder.include(latLngOfPlace);
                            LatLngBounds bounds = builder.build();

                            int width = getResources().getDisplayMetrics().widthPixels;
                            int padding = (int) (width * 0.2);
                            mLocationPin.setVisibility(View.GONE);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            mMap.animateCamera(cameraUpdate);

                            mMap.addMarker(new MarkerOptions().position(latLngOfPlace).title("Destination"));
                            mMap.addMarker(new MarkerOptions().position(currentMarkerLocation).title("Pick Up")).setIcon(getMarkerIconFromDrawable( getResources().getDrawable(R.drawable.ic_location_pin)));


                            getRouteToMarker(currentMarkerLocation, latLngOfPlace);

                            showSelectBottomSheetDialog();


                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i(TAG, "place not found: " + e.getMessage());
                            Log.i(TAG, "status code: " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectBottomSheetDialog();
            }
        });

        mCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endRide();
            }
        });
        setupLocationStream();
    }

    private void moveCamera(LatLng latLng, String title) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), "Pick Up");
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), "Pick Up");
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(getActivity(), "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void showSelectBottomSheetDialog() {
        if (selectRideSheetDialog == null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.select_ride_bottom_sheet_layout, null);
            final TextView carSelect = view.findViewById(R.id.car_select);
            final TextView bikeSelect = view.findViewById(R.id.bike_select);
            Button request = view.findViewById(R.id.btn_request);
            Button cancel = view.findViewById(R.id.btn_cancel);


            carSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bikeSelect.setBackground(null);
                    carSelect.setBackgroundResource(R.drawable.selected_ride_background);
                    mBikeSelect = false;
                    mCarSelect = true;
                }
            });

            bikeSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carSelect.setBackground(null);
                    bikeSelect.setBackgroundResource(R.drawable.selected_ride_background);
                    mBikeSelect = true;
                    mCarSelect = false;
                }
            });

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "CAR: " + mCarSelect + " BIKE: " + mBikeSelect);
                    selectRideSheetDialog.dismiss();
                    if (mLocationPin.getVisibility() == View.GONE) {
                        mMap.clear();
                        mLocationPin.setVisibility(View.VISIBLE);
                        moveCamera(currentMarkerLocation, "Pick Up");
                    } else
                        currentMarkerLocation = mMap.getCameraPosition().target;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("customerRequest");

                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(currentMarkerLocation.latitude, currentMarkerLocation.longitude), new
                            GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    //Do some stuff if you want to
                                }
                            });

                    if (mCustomerMarker != null)
                        mCustomerMarker = null;
                    mCancelRequest.setVisibility(View.VISIBLE);
                    mGo.setText("Getting your Driver....");
                    mRippleBg.startRippleAnimation();
                    if (mCarSelect){
                        getCloserDriver("Doctor");
                    }
                    else{
                        getCloserDriver("ambulance");
                    }


                }
            });


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectRideSheetDialog.dismiss();

                    getDeviceLocation();
                    mMap.clear();
                    mLocationPin.setVisibility(View.VISIBLE);
                }
            });

            selectRideSheetDialog = new BottomSheetDialog(getActivity());
            selectRideSheetDialog.setContentView(view);
            selectRideSheetDialog.setCanceledOnTouchOutside(false);
            selectRideSheetDialog.setCancelable(false);

        }

        selectRideSheetDialog.show();
    }



    private String driverFoundId;
    GeoQuery geoQuery;

    private void getCloserDriver(final String userfor) {

        FirebaseDatabase.getInstance().getReference("Users").child(userfor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float[] results = new float[1];
                getdoctorordriverList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (snapshot.child("status").getValue().equals("1")){

                        String[] getvv = snapshot.child("latlng").getValue().toString().split(",");
                        latitude = Double.parseDouble(getvv[0]);
                        longitude = Double.parseDouble(getvv[1]);
                        android.location.Location.
                                distanceBetween(currentMarkerLocation.latitude, currentMarkerLocation.longitude, latitude, longitude, results);

                        float distanceInMeters = results[0];
                        boolean isWithin10km = distanceInMeters < 5000;
                        if (isWithin10km) {
                            if(userfor.equals("ambulance")){
                                getdoctorordriverList.add(new getdoctorordriver(snapshot.child("name").getValue().toString(),
                                        snapshot.child("profileImageUrl").getValue().toString(),snapshot.child("address").getValue().toString(),
                                        snapshot.child("contact").getValue().toString(),
                                        snapshot.getKey(),userfor));
                            }
                            else{
                                getdoctorordriverList.add(new getdoctorordriver(snapshot.child("name").getValue().toString(),
                                        snapshot.child("profileImageUrl").getValue().toString(),snapshot.child("address").getValue().toString(),
                                        snapshot.child("contact").getValue().toString(),"",snapshot.child("specialization").getValue().toString(),
                                        snapshot.getKey(),userfor));
                            }


                        }
                    }
                }
                if (getdoctorordriverList.size() >0){

                    relat.setVisibility(View.GONE);
                    NearbydriverorDoctoradapter nearbydriverorDoctoradapter = new NearbydriverorDoctoradapter(getdoctorordriverList,getActivity());
                    recyclerView.setAdapter(nearbydriverorDoctoradapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else{

                    driverNotFound();
                    Toast.makeText(getActivity(), "No Driver Found!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    DatabaseReference driverLocRef;
    private ValueEventListener driverLocRefListener;
    private Marker marker;


    public static BitmapDescriptor getBitmapDesFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmapICon = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
        Canvas canvas = new Canvas(bitmapICon);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmapICon);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getRouteToMarker(LatLng place1, LatLng place2) {



        routing = new Routing.Builder()
                .key(getResources().getString(R.string.GOOGLE_API))
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(place1, place2)
                .build();
        routing.execute();
    }


    @Override
    public void onRoutingFailure(RouteException e) {
    try{
        if (e != null) {
            Log.i(TAG, e.getMessage());
        } else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    catch (Exception ee){}
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        chat.setVisibility(View.VISIBLE);
        Log.d("LOCATION TRACKING",route.size()+"");
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorPrimary));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

        }
    }


    private void driverDocFound(){
        mRippleBg.stopRippleAnimation();
        mCancelRequest.setVisibility(View.GONE);
        mMaterialSearchBar.setVisibility(View.GONE);
        mGo.setVisibility(View.GONE);
        mLocationPin.setVisibility(View.GONE);
        relat.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void driverNotFound() {
        mCancelRequest.setVisibility(View.GONE);
        mMaterialSearchBar.setVisibility(View.VISIBLE);
        mGo.setVisibility(View.VISIBLE);
        mGo.setText(getResources().getString(R.string.lets_go));
        mMap.clear();
        mLocationPin.setVisibility(View.VISIBLE);
        mRippleBg.stopRippleAnimation();
    }

    private void endRide() {

        driverNotFound();
        chat.setVisibility(View.GONE);
        mGo.setVisibility(View.VISIBLE);
        //dothis
        mCustomerMarker = null;
        mHelpMarker = null;
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child("Patient")
                .child(FirebaseAuth.getInstance().getUid())
                .child("status")
                .setValue("1");

    }

    @Override
    public void onRoutingCancelled() {

    }



    private  void setupLocationStream(){
        DatabaseReference   trackingRequest = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child("Patient")
                .child(FirebaseAuth.getInstance().getUid())
                .child("dora");

        trackingRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    listenForLocationStream(dataSnapshot.getValue().toString());
                }
                else{
                    endRide();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listenForLocationStream(final String uid){


        DatabaseReference customerRequest = FirebaseDatabase.getInstance()
                .getReference("customerRequest")
                .child(FirebaseAuth.getInstance().getUid())
                .child("l");

        customerRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("0").getValue().toString())
                            , Double.parseDouble(dataSnapshot.child("1").getValue().toString()));

                    mCustomerMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Me"));

                    driverDocFound();
                    makeRoute(uid, latLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void makeRoute(String uid, final LatLng latLng){
        DatabaseReference tracking = FirebaseDatabase.getInstance()
                .getReference("Tracking")
                .child(uid);

        tracking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String lat = dataSnapshot.child("lat").getValue().toString();
                    String lng = dataSnapshot.child("lng").getValue().toString();
                    LatLng latLng1 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    if (mHelpMarker == null) {
                        mCustomerMarker.setIcon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_location_pin)));
                        mHelpMarker = mMap.addMarker(new MarkerOptions().position(latLng1).title("Medical Assistance"));
                        mHelpMarker.setIcon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.hospital_opt)));
                        latLngBoundCamera(latLng1,latLng);
                    } else {
                        mHelpMarker.setPosition(latLng1);
                    }


                    getRouteToMarker(latLng1, latLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void latLngBoundCamera(LatLng l, LatLng ln){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(l);
        builder.include(ln);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width * 0.2);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cameraUpdate);
    }

}