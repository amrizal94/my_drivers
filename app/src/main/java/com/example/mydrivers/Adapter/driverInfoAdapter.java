package com.example.mydrivers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydrivers.Model.userModel;
import com.example.mydrivers.R;
import com.example.mydrivers.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class driverInfoAdapter extends RecyclerView.Adapter<driverInfoAdapter.ViewHolder>{
    private ArrayList<userModel> driverList;
    private Context context;
    private SelectListener listener;

    public driverInfoAdapter(ArrayList<userModel> driverList, Context context, SelectListener listener) {
        this.driverList = driverList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        userModel model = driverList.get(position);
        holder.userName.setText(model.getUserName());
        holder.userEmail.setText(model.getUserEmail());
        holder.userNumber.setText(model.getUserNumber());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(model);
            }
        });
        String location = ("Latitude: " + model.getLatitude() + "\nLongitude: " + model.getLongitude() + "\n" + model.getRealLocation());
        holder.userLocation.setText(location);
        Picasso.get().load(model.getImageProfile()).into(holder.userProfile);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userProfile;
        private TextView userName, userEmail, userNumber, userLocation;
        public CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.iv_users_profile);
            userName = itemView.findViewById(R.id.tv_userName);
            userEmail = itemView.findViewById(R.id.tv_emailUser);
            userNumber = itemView.findViewById(R.id.tv_number);
            userLocation = itemView.findViewById(R.id.tv_location);
            cardView = itemView.findViewById(R.id.main_container);
        }
    }
}