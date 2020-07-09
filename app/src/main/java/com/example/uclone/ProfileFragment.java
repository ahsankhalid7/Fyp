package com.example.uclone;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private EditText mEmail, mContact, mName;
    PreferencesClass preferencesClass;
    private boolean flag =  false;
    private  int SELECT_IMAGE = 102;
    private Button mBtnRegister;
    private DatabaseReference mCustomerDatabaseReference;
    private ImageView mProfileImage;
    private Uri resultUri;
    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view  = inflater.inflate(R.layout.fragment_profile, container, false);
        preferencesClass = new PreferencesClass(getActivity());
        mEmail = view.findViewById(R.id.email);
        mBtnRegister = view.findViewById(R.id.btn_register);
        mContact = view.findViewById(R.id.contact);
        mName = view.findViewById(R.id.name);
        mProfileImage = view.findViewById(R.id.profile_image);
//        mEmail.setText(preferencesClass.getUSER_EMAIL());
//        mContact.setText(preferencesClass.getUserContact());
//        mName.setText(preferencesClass.getUSER_NAME());
            FirebaseDatabase.getInstance().getReference("Users").child(preferencesClass.get_USERFOR()).child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Glide.with(getActivity())
                            .load(dataSnapshot.child("profileImageUrl").getValue().toString())
                            .placeholder(R.drawable.upload_profile)
                            .override(300, 200)
                            .into(mProfileImage);


                        mEmail.setText(dataSnapshot.child("email").getValue().toString());
                       mContact.setText(dataSnapshot.child("contact").getValue().toString());
                       mName.setText(dataSnapshot.child("name").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (flag){uploadImage();}
                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getUid()).child("name").setValue(mName.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getUid()).child("contact").setValue(mContact.getText().toString());
                Toast.makeText(getActivity(), "ProfileUpdated", Toast.LENGTH_SHORT).show();

            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    flag = true;
                    resultUri = data.getData();
                    Glide.with(getActivity())
                            .load(resultUri)
                            .placeholder(R.drawable.upload_profile)
                            .override(300, 200)
                            .into(mProfileImage);

                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
            }
        }
    }

    private void uploadImage() {
        mCustomerDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getUid());
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getUid());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
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
                    Toast.makeText(getActivity(), "no succes", Toast.LENGTH_SHORT).show();
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
                    //savePreferences();
                } else {
                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
