package com.project.niko.fortajava.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.niko.fortajava.main.ActivityViewResult;
import com.project.niko.fortajava.main.MainActivity;
import com.project.niko.fortajava.Model.Category;
import com.project.niko.fortajava.R;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private List<Category> categoryList;
    private Context context;

    public AdapterCategory(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterCategory.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCategory.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tv_category_name.setText(categoryList.get(position).getCategory_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", categoryList.get(position).getCategory_name());
                intent.putExtra("key", "category");
                intent.putExtra("value", categoryList.get(position).getCategory_id());
                intent.putExtra("time", ((MainActivity)context).startTime);
                intent.setClass(context, ActivityViewResult.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_category_name;
        MyViewHolder(View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
