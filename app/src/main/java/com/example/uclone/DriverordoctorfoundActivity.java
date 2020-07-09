package com.example.uclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import static com.example.uclone.MapFragment.getdoctorordriverList;

public class DriverordoctorfoundActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverordoctorfound);


        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        NearbydriverorDoctoradapter nearbydriverorDoctoradapter = new NearbydriverorDoctoradapter(getdoctorordriverList,DriverordoctorfoundActivity.this);
        recyclerView.setAdapter(nearbydriverorDoctoradapter);

//
//        recyclerView = findViewById(R.id.edit_recyclerview);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(llm);
    }
}
