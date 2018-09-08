package com.project.niko.fortajava.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.niko.fortajava.main.ActivityViewDetail;
import com.project.niko.fortajava.Helper.PicassoClient;
import com.project.niko.fortajava.Model.Restaurant;
import com.project.niko.fortajava.R;

import java.util.List;

public class AdapterRestaurant extends RecyclerView.Adapter<AdapterRestaurant.MyViewHolder>{

    private List<Restaurant> restaurantList;
    private Context context;

    public AdapterRestaurant(List<Restaurant> restaurantList, Context context) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterRestaurant.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_restaurant, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AdapterRestaurant.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tv_restorant_name.setText(restaurantList.get(position).getName());
        holder.tv_restourant_cousines.setText(restaurantList.get(position).getCuisines());
        holder.tv_restourant_address.setText(restaurantList.get(position).getLocation().getLocalityVerbose());
        holder.tv_price_approx.setText("Rp." + restaurantList.get(position).getAverageCostForTwo() + " for two people(approx..)");
        holder.tv_rating.setText(restaurantList.get(position).getUserRating().getAggregateRating());
        holder.tv_rating.setBackgroundColor(Color.parseColor("#"+restaurantList.get(position).getUserRating().getRatingColor()));
        if(!restaurantList.get(position).getFeaturedImage().isEmpty()){
            PicassoClient.downloadImage(context, restaurantList.get(position).getFeaturedImage(), holder.img_photo);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityViewDetail.class);
                intent.putExtra("res_id", restaurantList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_restorant_name, tv_restourant_cousines,
                tv_restourant_address, tv_price_approx, tv_rating;
        private ImageView img_photo;
        MyViewHolder(View itemView) {
            super(itemView);
            tv_restorant_name = itemView.findViewById(R.id.tv_restorant_name);
            tv_restourant_cousines = itemView.findViewById(R.id.tv_restorant_cuisines);
            tv_restourant_address = itemView.findViewById(R.id.tv_restorant_address);
            tv_price_approx = itemView.findViewById(R.id.tv_price_approx);
            img_photo = itemView.findViewById(R.id.img_photo);
            tv_rating = itemView.findViewById(R.id.tv_rating);
        }
    }
}
