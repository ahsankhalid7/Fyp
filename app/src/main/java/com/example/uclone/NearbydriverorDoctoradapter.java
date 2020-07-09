package com.example.uclone;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NearbydriverorDoctoradapter extends  RecyclerView.Adapter<NearbydriverorDoctoradapter.ViewHolder> {
    private List<getdoctorordriver> getdoctorordriverList;
    APIService apiService;
    private String getaccepteddid = "";
    private Context context;
    PreferencesClass preferencesClass;
    public NearbydriverorDoctoradapter(List<getdoctorordriver> getdoctorordriverList, Context context) {
        this.getdoctorordriverList = getdoctorordriverList;
        this.context = context;
    }

    @NonNull
    @Override
    public NearbydriverorDoctoradapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearbydriverordoctorlistitem, parent, false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        FirebaseDatabase.getInstance().getReference("Users").child("Patient").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("acceptedid")){
                  getaccepteddid= dataSnapshot.child("acceptedid").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NearbydriverorDoctoradapter.ViewHolder holder, final int position) {

        holder.request.setText("Request");
        Glide.with(context).asBitmap().load(getdoctorordriverList.get(position).getImg()).apply(new RequestOptions().centerCrop().placeholder(R.drawable.avatar)).into(holder.magentImage);
        holder.magentname.setText(getdoctorordriverList.get(position).getName());
        holder.magentPhopne.setText(getdoctorordriverList.get(position).getPhonenumber());
        holder.address_text.setText(getdoctorordriverList.get(position).getAddress());
        holder.specialization.setText(getdoctorordriverList.get(position).getSpecialization());
        preferencesClass = new PreferencesClass((Activity) context);
        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Request").child(getdoctorordriverList.get(position).getUserfor()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getUid())){
                            Toast.makeText(context, "You have requested the Dr please wait for the response", Toast.LENGTH_LONG).show();
                        }
                        else{

                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setTitle("Please wait");
                            progressDialog.show();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("client_name",preferencesClass.getUSER_NAME());
                            hashMap.put("image",preferencesClass.getUserImage());
                            hashMap.put("address",preferencesClass.getaddress2());
                            hashMap.put("latlng",preferencesClass.getlatng());
                            hashMap.put("status","0");
                            hashMap.put("userid",FirebaseAuth.getInstance().getUid());

                            FirebaseDatabase.getInstance().getReference("Requests").child(getdoctorordriverList.get(position).getUserfor()).child(getdoctorordriverList.get(position).getId()).child(FirebaseAuth.getInstance().getUid()).setValue(hashMap);
                            sendNotifiaction(getdoctorordriverList.get(position).getId(),getdoctorordriverList.get(position).getName(),"I need a checkup");
                            holder.request.setText("Requested");
                            holder.request.setEnabled(false);
                            progressDialog.dismiss();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
    private void sendNotifiaction(final String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getUid(), R.mipmap.ic_launcher, username + ": " + message, "Patient Request",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

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
                                    Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return getdoctorordriverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView magentImage;
        private TextView magentname, magentPhopne, address_text,specialization;
        private Button request;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            magentImage = itemView.findViewById(R.id.agentImage);
            magentname = itemView.findViewById(R.id.agentname);
            magentPhopne = itemView.findViewById(R.id.agentPhopne);
            address_text = itemView.findViewById(R.id.address_text);
            specialization = itemView.findViewById(R.id.specialization);

            request = itemView.findViewById(R.id.request);

        }
    }
}
