package com.project.niko.fortajava.Helper;

import android.content.Context;
import android.widget.ImageView;

import com.project.niko.fortajava.R;
import com.squareup.picasso.Picasso;

public class PicassoClient {

    public static void downloadImage(final Context context, final String url, final ImageView img) {
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.no_image_product_detail)
                .into(img);
    }
}
