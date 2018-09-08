package com.project.niko.fortajava.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.niko.fortajava.Helper.PicassoClient;
import com.project.niko.fortajava.Model.UserReview;
import com.project.niko.fortajava.R;

import java.util.List;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.MyViewHolder> {
    private List<UserReview> userReviewList;
    private Context context;

    public AdapterReview(List<UserReview> userReviewList, Context context) {
        this.userReviewList = userReviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_review, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_name.setText(userReviewList.get(position).getUser().getName());
        holder.tv_time.setText(userReviewList.get(position).getReviewTimeFriendly());
        holder.tv_review_text.setText(userReviewList.get(position).getReviewText());
        PicassoClient.downloadImage(context, userReviewList.get(position).getUser().getProfileImage(), holder.img_photo);
    }

    @Override
    public int getItemCount() {
        return userReviewList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_photo;
        private TextView tv_name, tv_time, tv_review_text;

        MyViewHolder(View itemView) {
            super(itemView);
            img_photo = itemView.findViewById(R.id.img_photo);
            tv_name = itemView.findViewById(R.id.tv_user_name);
            tv_time = itemView.findViewById(R.id.tv_review_time);
            tv_review_text = itemView.findViewById(R.id.tv_review_teks);
        }
    }
}
