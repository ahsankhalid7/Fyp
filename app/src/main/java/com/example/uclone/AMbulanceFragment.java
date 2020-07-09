package com.example.uclone;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.uclone.MapFragment.getdoctorordriverList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AMbulanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AMbulanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView requesttextview;
    private RecyclerView requests_recyclerview;
    private PreferencesClass preferencesClass;
    private List<getdoctorordriver> getdoctorordriverslist;
    APIService apiService;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mBtnFinish;
    private RequetAdapter nearbydriverorDoctoradapter;
    DatabaseReference customerRequest;
    String userId;
    ValueEventListener customerRequestTrack = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("0").getValue().toString())
                    ,Double.parseDouble(dataSnapshot.child("1").getValue().toString()));

            streamLocation(latLng,userId);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    public AMbulanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AMbulanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AMbulanceFragment newInstance(String param1, String param2) {
        AMbulanceFragment fragment = new AMbulanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_a_mbulance, container, false);

        requests_recyclerview = view.findViewById(R.id.requests_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        requests_recyclerview.setLayoutManager(llm);
        preferencesClass = new PreferencesClass(getActivity());
        getdoctorordriverslist = new ArrayList<>();
        requesttextview  = view.findViewById(R.id.requesttextview);
        mBtnFinish = view.findViewById(R.id.finishThings);
        mBtnFinish.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("Requests").child(preferencesClass.get_USERFOR()).child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                getdoctorordriverslist = new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    getdoctorordriverslist.add(new getdoctorordriver(snapshot.child("client_name").getValue().toString(),snapshot.child("address").getValue().toString(),snapshot.child("address").getValue().toString(),"",snapshot.child("latlng").getValue().toString(),"",snapshot.getKey(),snapshot.child("status").getValue().toString()));
                    nearbydriverorDoctoradapter = new RequetAdapter(getdoctorordriverslist,getActivity());
                    requests_recyclerview.setAdapter(nearbydriverorDoctoradapter);

                }
                if (getdoctorordriverslist.size()==0){
                    requesttextview.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        checkForTrackingRequest();


        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child("ambulance")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("status")
                        .setValue("1");

                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child("ambulance")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("patient")
                        .removeValue();

                FirebaseDatabase.getInstance()
                        .getReference("Tracking")
                        .child(FirebaseAuth.getInstance().getUid())
                        .removeValue();

                final DatabaseReference reqReference = FirebaseDatabase.getInstance()
                        .getReference("Requests")
                        .child("ambulance")
                        .child(FirebaseAuth.getInstance().getUid());

                reqReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("CHECK",dataSnapshot.toString());
                        for (DataSnapshot snapshot:
                                dataSnapshot.getChildren()) {
                            Log.d("Check",snapshot.toString());
                            String Userid = snapshot.child("userid").getValue().toString();
                            FirebaseDatabase.getInstance()
                                    .getReference("Myhistory")
                                    .child(Userid)
                                    .push()
                                    .setValue(dataSnapshot.getValue());

                            FirebaseDatabase.getInstance()
                                    .getReference("Myhistory")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .push()
                                    .setValue(dataSnapshot.getValue());

                            FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child("Patient")
                                    .child(Userid)
                                    .child("dora")
                                    .removeValue();
                        }




                        reqReference.removeValue();
                        stopLocationStream();
                        customerRequest.removeEventListener(customerRequestTrack);

                        mBtnFinish.setVisibility(View.GONE);
                        getdoctorordriverslist.clear();
                        nearbydriverorDoctoradapter.notifyDataSetChanged();
                        requesttextview.setVisibility(View.VISIBLE);
                        preferencesClass.setArrivedNot("false");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        return view;

    }
    private void checkForTrackingRequest(){
        DatabaseReference trackingRequest = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(preferencesClass.get_USERFOR())
                .child(FirebaseAuth.getInstance().getUid())
                .child("patient");

        trackingRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    customerRequest = FirebaseDatabase.getInstance()
                            .getReference("customerRequest")
                            .child(dataSnapshot.getValue().toString())
                            .child("l");
                    userId = dataSnapshot.getValue().toString();

                    customerRequest.addValueEventListener(customerRequestTrack);


                }
                else
                    stopLocationStream();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void streamLocation(final LatLng latLng, final String uid){
        final float[] results = new float[1];
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();

                HashMap hashMap = new HashMap<>();
                hashMap.put("lat",location.getLatitude());
                hashMap.put("lng",location.getLongitude());
                if(FirebaseAuth.getInstance().getUid() != null) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Tracking")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(hashMap);
                }
                LatLng latLng1 = new LatLng(location.getLatitude(),location.getLongitude());

                if(preferencesClass.getArrivedNot().equals("false")) {
                    float distance = getDistance(latLng1, latLng, results);
                    Log.d("DISTANCE",distance+"   "+uid);
                    if (getDistance(latLng1, latLng, results) < 50) {
                        sendNotifiaction(uid, "Patient", "Please meet your Doctor/Ambulance outside");
                        PreferencesClass preferencesClass = new PreferencesClass(getActivity());
                        preferencesClass.setArrivedNot("true");
                        mBtnFinish.setVisibility(View.VISIBLE);
                    }
                    Log.d("ARRIVED","NOT SEND");
                }
                else{
                    mBtnFinish.setVisibility(View.VISIBLE);
                    Log.d("ARRIVED","SEND");
                }

            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationStream(){
        if(locationCallback != null) {
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Log.d("CALLBAKC","REMoVED");
        }
    }

    private float getDistance(LatLng latlng, LatLng latlng2,float[] results){
        android.location.Location.
                distanceBetween(latlng.latitude, latlng.longitude, latlng2.latitude, latlng2.longitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters;


    }


    private void sendNotifiaction(final String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getUid(), R.mipmap.ic_launcher, username + ": " + message, "Order Acceptance",
                            receiver);
                    Sender sender = new Sender(data, token.getToken());
//no
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponce>() {
                                @Override
                                public void onResponse(Call<MyResponce> call, Response<MyResponce> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponce> call, Throwable t) {
                                    Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
