package com.example.uclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button mSendMail;
    private EditText mEmail;
    FirebaseAuth mAuth;
    private HelpingMethods helpingMethods;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mSendMail = findViewById(R.id.button_sendemail);
        mEmail = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        helpingMethods = new HelpingMethods(ForgotPasswordActivity.this);
        dialog = helpingMethods.createProgressDialog("Sending","Sending email to your account.");

        mSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (validateData()) {
                    dialog.show();
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), "***********")
                            .addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                try {
                                                    throw task.getException();
                                                }
                                                // if user enters wrong email.
                                                catch (FirebaseAuthInvalidUserException invalidEmail) {
                                                    helpingMethods.snackbarMessage("Email does not exist", view, HelpingMethods.MSG_ERROR);
                                                    dialog.cancel();
                                                    return;
                                                    // TODO: take your actions!
                                                }
                                                // if user enters wrong password.
                                                catch (FirebaseAuthInvalidCredentialsException wrongPassword) {

                                                    mSendMail(view);


                                                    // TODO: Take your action
                                                } catch (Exception e) {
                                                    //Log.d(TAG, "onComplete: " + e.getMessage());
//                                                Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                                                    helpingMethods.snackbarMessage(e.getMessage(), view, HelpingMethods.MSG_ERROR);
                                                    dialog.cancel();
                                                }
                                            }
                                        }
                                    }
                            );


                }
            }
        });


    }

    private boolean validateData(){

        boolean flag = true;
        String email = mEmail.getText().toString().trim();

        if(email.isEmpty()) {
            mEmail.setError("Email Required");
            flag = false;
        }else{
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()){
                mEmail.setError("Invalid Email");
                flag = false;
            }
        }

        return flag;
    }

    private void mSendMail(final View view) {

        mAuth.sendPasswordResetEmail(mEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            helpingMethods.snackbarMessage("A reset password email has been sent",view,HelpingMethods.MSG_SUCCESS);
                            dialog.cancel();
                            mEmail.getText().clear();
                        } else {
                            dialog.cancel();
                            helpingMethods.snackbarMessage("Error while sending reset email",view,HelpingMethods.MSG_ERROR);
                        }
                    }
                });
    }
}
