package com.example.uclone;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Historyadapter extends  RecyclerView.Adapter<Historyadapter.ViewHolder> {
    private Context context;
    Geocoder geocoder;
    List<Address> addresses;
    private List<getdoctorordriver> getdoctorordriverList;
    public Historyadapter(List<getdoctorordriver> getdoctorordriverList, Context context) {
        this.getdoctorordriverList = getdoctorordriverList;
        this.context = context;
    }

    @NonNull
    @Override
    public Historyadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historyitem, parent, false);
        return  new Historyadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(getdoctorordriverList.get(position).getName());

        Glide.with(context)
                .load(getdoctorordriverList.get(position).getImg())
                .placeholder(R.drawable.upload_profile)
                .override(300, 200)
                .into(holder.magentImage);


        geocoder = new Geocoder(context, Locale.getDefault());


        String[] get = getdoctorordriverList.get(position).getId().split(",");
        Double lati = Double.valueOf(get[0]);
        Double lng = Double.valueOf(get[1]);
        try {
            addresses = geocoder.getFromLocation(lati, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        holder.address.setText(address);


    }


    @Override
    public int getItemCount() {

        return getdoctorordriverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView,address;
        private CircleImageView magentImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            magentImage = itemView.findViewById(R.id.agentImage);
            textView = itemView.findViewById(R.id.agentname);
            address = itemView.findViewById(R.id.address_text);
        }
    }
}
