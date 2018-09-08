package com.project.niko.fortajava.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.niko.fortajava.Model.City;
import com.project.niko.fortajava.R;
import com.project.niko.fortajava.main.SearchActivity;

import java.util.List;

public class AdapterLocation extends RecyclerView.Adapter<AdapterLocation.MyViewHolder> {

    private List<City> cityList;
    private Context context;

    public AdapterLocation(List<City> cityList, Context context) {
        this.cityList = cityList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterLocation.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_location, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLocation.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tv_city.setText(cityList.get(position).getCityName());
        holder.tv_country.setText(cityList.get(position).getCityName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchActivity) context).getLocation(cityList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_city, tv_country;
        MyViewHolder(View itemView) {
            super(itemView);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_country = itemView.findViewById(R.id.tv_country);
        }
    }
}
