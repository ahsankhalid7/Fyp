package com.example.uclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import javax.xml.validation.Validator;

public class LoginActivity extends AppCompatActivity {


    private Button mBtnLogin, mBtnRegister,Patient_btn,dr_btn,ambulance_btn;
    private LinearLayout step1_linearlayout,step2_linearlayout;
    private EditText mEmail, mPassword;
    private TextView mForgotPassword;
    private FirebaseAuth mAuth;
    private String userfor = "";
    private HelpingMethods helpingMethods;
    private ProgressDialog progressDialog;
    Boolean preferencesCheck = false;
    PreferencesClass preferencesClass;
    DatabaseReference mCustomerReference;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


//for debug
            preferencesClass.setUserPassword(mPassword.getText().toString());
            FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getUid()).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
            preferencesClass.saveUser(dataSnapshot,userfor);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Patient_btn = findViewById(R.id.Patient_btn);
        dr_btn = findViewById(R.id.Doctor_btn);
        step1_linearlayout = findViewById(R.id.step1);
        step2_linearlayout = findViewById(R.id.step2);
        ambulance_btn = findViewById(R.id.Ambulance_Diver_btn);

        Patient_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step1_linearlayout.setVisibility(View.GONE);
                step2_linearlayout.setVisibility(View.VISIBLE);
                userfor = "Patient";
            }
        });
        dr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step1_linearlayout.setVisibility(View.GONE);
                step2_linearlayout.setVisibility(View.VISIBLE);
                userfor = "Doctor";
            }
        });
        ambulance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step1_linearlayout.setVisibility(View.GONE);
                step2_linearlayout.setVisibility(View.VISIBLE);
                userfor = "ambulance";
            }
        });

        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);

        mForgotPassword = findViewById(R.id.textView_forgotPassword);

        helpingMethods = new HelpingMethods(LoginActivity.this);

        progressDialog = helpingMethods.createProgressDialog("Logging In", "Please wait while we check your credentials.");

        mAuth = FirebaseAuth.getInstance();


        preferencesClass = new PreferencesClass(LoginActivity.this);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if (preferencesClass.getUSER_NAME() == null) {
                        progressDialog.show();
                        savePreferences();
                        //umair
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };


        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                final String email = "test1@gmail.com";
                final String password = "afraz123";
                if (validateLoginData()) {
//                    final String email =  mEmail.getText().toString().trim();
//                    final String password = mPassword.getText().toString().trim();

                    FirebaseDatabase.getInstance().getReference("Users").child(userfor).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean check = false;
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                if (dataSnapshot1.exists() ){

                                    if(dataSnapshot1.child("email").getValue().toString().equals(mEmail.getText().toString())){
                                        check = true;
                                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "sign in error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                progressDialog.cancel();
                                            }
                                            else{
                                                savePreferences();
                                            }
                                        }
                                    });
                                    }
                                }
                                else{
                                    progressDialog.dismiss();
                                }
                            }

                            if(!check)
                                Toast.makeText(LoginActivity.this,"User not found!",Toast.LENGTH_SHORT).show();



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





                }

            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("userfor",userfor);
                startActivity(intent);
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void savePreferences() {
        mCustomerReference = helpingMethods.getCustomerReference(userfor);
        preferencesCheck = true;
        mCustomerReference.addValueEventListener(valueEventListener);
    }

    private boolean validateLoginData() {

        boolean flag = true;
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

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
            mEmail.setError("Password Required");
            flag = false;
        }

        return flag;
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        step2_linearlayout.setVisibility(View.GONE);
        step1_linearlayout.setVisibility(View.VISIBLE);
    }
}
