package com.example.uclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.uclone.MapFragment.getdoctorordriverList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<getdoctorordriver> getdoctorordriverList;
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.history);
        getdoctorordriverList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        FirebaseDatabase.getInstance().getReference("Myhistory").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshots: dataSnapshot.getChildren()){
                        for (DataSnapshot snapshot:snapshots.getChildren()){
                            getdoctorordriverList.add(new getdoctorordriver(snapshot.child("address").getValue().toString(),
                                    snapshot.child("client_name").getValue().toString(),
                                    snapshot.child("image").getValue().toString(),
                                    snapshot.child("latlng").getValue().toString(),
                                    snapshot.child("status").getValue().toString()
                                    ));
                        }
                    }
                    Historyadapter nearbydriverorDoctoradapter = new Historyadapter(getdoctorordriverList,getActivity());
                    recyclerView.setAdapter(nearbydriverorDoctoradapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }
}
