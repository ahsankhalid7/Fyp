package com.example.uclone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.directions.route.RoutingListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private PreferencesClass preferencesClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        preferencesClass = new PreferencesClass(this);
        if(FirebaseAuth.getInstance().getUid() != null)
            FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getUid()).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_email = hView.findViewById(R.id.user_email);
        TextView nav_name = hView.findViewById(R.id.user_name);
        CircleImageView nav_profilePic = hView.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(preferencesClass.getUserImage())
                .placeholder(R.drawable.upload_profile)
                .into(nav_profilePic);
        nav_email.setText(preferencesClass.getUSER_EMAIL());
        nav_name.setText(preferencesClass.getUSER_NAME());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        if (preferencesClass.get_USERFOR().equals("Doctor")){

            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.blackToggle));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Request_Fragment()).commit();
        }
        else if(preferencesClass.get_USERFOR().equals("ambulance"))
        {
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.blackToggle));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AMbulanceFragment()).commit();
        }
        else{
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.blackToggle));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment()).commit();
        }
        ;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            if (preferencesClass.get_USERFOR().equals("Doctor")){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Request_Fragment()).commit();
            }
            else if(preferencesClass.get_USERFOR().equals("Ambulance"))
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AMbulanceFragment()).commit();
            }
            else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment()).commit();
            }

        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

        } else if (id == R.id.history) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HistoryFragment()).commit();

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}