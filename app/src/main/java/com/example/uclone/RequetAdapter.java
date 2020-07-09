package com.example.uclone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.uclone.MapFragment.getdoctorordriverList;

public class RequetAdapter extends  RecyclerView.Adapter<RequetAdapter.ViewHolder> {
    private List<getdoctorordriver> getdoctorordriverList;
    APIService apiService;
    private Context context;
    PreferencesClass preferencesClass;
    public RequetAdapter(List<getdoctorordriver> getdoctorordriverList, Context context) {
        this.getdoctorordriverList = getdoctorordriverList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearbydriverordoctorlistitem, parent, false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        preferencesClass = new PreferencesClass((Activity) context);
        return new RequetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.button.setText("Accept");
        holder.linearspecial.setVisibility(View.GONE);
        if (getdoctorordriverList.get(position).getUserfor().equals("1")){
            holder.Directopm.setVisibility(View.VISIBLE);
            holder.button.setText("Accepted");
            holder.button.setEnabled(false);

        }
        else{
            holder.Directopm.setVisibility(View.GONE);
        }
        Glide.with(context).asBitmap().load(getdoctorordriverList.get(position).getImg()).apply(new RequestOptions().centerCrop().placeholder(R.drawable.avatar)).into(holder.magentImage);
        holder.magentname.setText(getdoctorordriverList.get(position).getName());
        holder.address_text.setText(getdoctorordriverList.get(position).getAddress());
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MessagingActivity.class);
                intent.putExtra("driverId",getdoctorordriverList.get(position).getId());
                intent.putExtra("driverName","Medical assitant");
                context.startActivity(intent);
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Requests").child(preferencesClass.get_USERFOR()).child(FirebaseAuth.getInstance().getUid()).child(getdoctorordriverList.get(position).getId()).child("status").setValue("1");
                holder.Directopm.setVisibility(View.VISIBLE);
                sendNotifiaction(getdoctorordriverList.get(position).getId(),getdoctorordriverList.get(position).getName(),preferencesClass.get_USERFOR()+" Accepted your request");
                Toast.makeText(context, "achaaa", Toast.LENGTH_SHORT).show();
                holder.button.setEnabled(false);
                holder.button.setText("Accepted");

                FirebaseDatabase.getInstance().
                        getReference("Users")
                        .child(preferencesClass.get_USERFOR())
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("status").setValue("0");

                FirebaseDatabase.getInstance().
                        getReference("Users")
                        .child("Patient")
                        .child(getdoctorordriverList.get(position).getId())
                        .child("status").setValue("0");

                FirebaseDatabase.getInstance().
                        getReference("Users")
                        .child(preferencesClass.get_USERFOR())
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("patient").setValue(getdoctorordriverList.get(position).getId());

                FirebaseDatabase.getInstance().
                        getReference("Users")
                        .child("Patient")
                        .child(getdoctorordriverList.get(position).getId())
                        .child("dora").setValue(FirebaseAuth.getInstance().getUid());
            }
        });
        holder.Directopm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+getdoctorordriverList.get(position).getHospitalassociation());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
             context.startActivity(mapIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getdoctorordriverList.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        private CircleImageView magentImage;
        private TextView magentname, magentPhopne, address_text,specialization;
        private Button request,Directopm,chat;
        private LinearLayout linearspecial;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearspecial = itemView.findViewById(R.id.linearspecial);
            button = itemView.findViewById(R.id.request);
            magentImage = itemView.findViewById(R.id.agentImage);
            magentname = itemView.findViewById(R.id.agentname);
            magentPhopne = itemView.findViewById(R.id.agentPhopne);
            address_text = itemView.findViewById(R.id.address_text);
            specialization = itemView.findViewById(R.id.specialization);
            chat = itemView.findViewById(R.id.chat);
            Directopm = itemView.findViewById(R.id.Directopm);


            request = itemView.findViewById(R.id.request);
        }
    }
}
