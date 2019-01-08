package com.project.niko.fortajava.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.niko.fortajava.Adapter.AdapterDailyMenu;
import com.project.niko.fortajava.Adapter.AdapterReview;
import com.project.niko.fortajava.Helper.HelperAPI;
import com.project.niko.fortajava.Model.DailyMenu;
import com.project.niko.fortajava.Model.Restaurant;
import com.project.niko.fortajava.Model.User;
import com.project.niko.fortajava.Model.UserReview;
import com.project.niko.fortajava.R;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ActivityViewDetail extends AppCompatActivity implements ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {

    private SliderLayout slider = null;

    private TextView tv_userRating = null;
    private TextView tv_restaurant_name = null;
    private TextView tv_restorant_address = null;
    private TextView tv_restorant_cuisines = null;
    private TextView tv_average_cost = null;
    private TextView tv_menu_no_data = null;
    private TextView tv_review = null;
    private RecyclerView rv_daily_menu = null;
    private RecyclerView rv_review = null;

    private LinearLayout ln_review = null;
    private ImageView img_arrow = null;
    private ProgressBar progressBar = null;

    private List<UserReview> userReviewList = new ArrayList<>();
    private List<DailyMenu> dailyMenuList = new ArrayList<>();

    private AdapterReview reviewAdapter = null;
    private AdapterDailyMenu adapterDailyMenu = null;

    private ProgressDialog progressDialog;

    private boolean showReview = false;
    private long startTime = 0, loadTime = 0;

    private Restaurant restaurant = new Restaurant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTime = System.currentTimeMillis();
        setContentView(R.layout.activity_view_detail);

        init();

        reviewAdapter = new AdapterReview(userReviewList, this);
        rv_review.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_review.setAdapter(reviewAdapter);
        rv_daily_menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getDetail();
    }

    private void init(){
        slider = this.findViewById(R.id.slider);
        tv_average_cost = this.findViewById(R.id.tv_average_cost);
        tv_restorant_address = this.findViewById(R.id.tv_restorant_address);
        tv_restorant_cuisines = this.findViewById(R.id.tv_restaurant_cuisines);
        tv_userRating = this.findViewById(R.id.tv_userRating);
        rv_daily_menu = this.findViewById(R.id.rv_daily_menu);
        rv_review = this.findViewById(R.id.rv_review);
        tv_restaurant_name = this.findViewById(R.id.tv_restorant_name);
        tv_menu_no_data = this.findViewById(R.id.tv_menu_no_data);
        tv_review = this.findViewById(R.id.tv_review);
        ln_review = this.findViewById(R.id.ln_review);
        img_arrow = this.findViewById(R.id.img_arrow);
        progressBar = this.findViewById(R.id.progressBar);

        this.findViewById(R.id.btn_direction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityViewDetail.this, MapsActivity.class);
                intent.putExtra("originLat", -6.907745);
                intent.putExtra("originLng", 107.609444);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });

        ln_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showReview){
                    showReview = false;
                    img_arrow.startAnimation(collapse());
                    rv_review.setVisibility(View.GONE);
                }
                else {
                    showReview = true;
                    img_arrow.startAnimation(expand());
                    progressBar.setVisibility(View.VISIBLE);
                    rv_review.setVisibility(View.VISIBLE);
                    userReviewList.clear();
                    getAllReviews();
                }
            }
        });
    }

    private void getDetail(){
        progressDialog = ProgressDialog.show(this, null, "Loading");
        RequestParams params = new RequestParams();
        params.put("res_id", getIntent().getStringExtra("res_id"));
        HelperAPI.get("restaurant", params, new JsonHttpResponseHandler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long exeTime = System.currentTimeMillis();
                super.onSuccess(statusCode, headers, response);
                try {
                    restaurant.setName(response.getString("name"));
                    com.project.niko.fortajava.Model.Location location = new com.project.niko.fortajava.Model.Location();
                    location.setAddress(response.getJSONObject("location").getString("address"));
                    location.setLatitude(response.getJSONObject("location").getDouble("latitude"));
                    location.setLongitude(response.getJSONObject("location").getDouble("longitude"));
                    restaurant.setLocation(location);
                    tv_restaurant_name.setText(restaurant.getName());
                    tv_restorant_address.setText(restaurant.getLocation().getAddress());
                    restaurant.setCuisines(response.getString("cuisines"));
                    tv_restorant_cuisines.setText(restaurant.getCuisines());
                    tv_average_cost.setText(response.getString("average_cost_for_two") + " " + response.getString("currency")
                            + " for two people (approx.)");
                    tv_userRating.setText(response.getJSONObject("user_rating").getString("aggregate_rating"));
                    tv_userRating.setBackgroundColor(Color.parseColor("#" + response.getJSONObject("user_rating").getString("rating_color")));
                    restaurant.setFeaturedImage(response.getString("featured_image"));
                    initSlider(response.getString("featured_image"));
                    //getDailyMenu();
                    tv_menu_no_data.setVisibility(View.VISIBLE);
                    startTime += System.currentTimeMillis() - exeTime;
                    getAllReviews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getDailyMenu(){
        RequestParams params = new RequestParams();
        params.put("res_id", getIntent().getStringExtra("res_id"));
        HelperAPI.get("dailymenu", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //DailyMenu dailyMenu = new DailyMenu();
                System.out.println("Daily menu " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getAllReviews(){
        RequestParams params = new RequestParams();
        params.put("res_id", getIntent().getStringExtra("res_id"));
        params.put("start", 0);
        params.put("count", 20);

        HelperAPI.get("reviews", params, new JsonHttpResponseHandler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long exeTime = System.currentTimeMillis();
                super.onSuccess(statusCode, headers, response);
                try {
                    for(int i = 0 ; i < response.getJSONArray("user_reviews").length() ; i++){
                        tv_review.setText("Review (" + response.getInt("reviews_shown") + "/" + response.getInt("reviews_count") + ")");
                        UserReview review = new UserReview();
                        review.setRating(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("rating"));
                        review.setReviewText(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("review_text"));
                        review.setId(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("id"));
                        review.setRatingColor(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("rating_color"));
                        review.setReviewTimeFriendly(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("review_time_friendly"));
                        review.setRatingText(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getString("rating_text"));
                        review.setTimestamp(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("timestamp"));
                        review.setLikes(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("likes"));
                        User user = new User();
                        user.setName(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getJSONObject("user").getString("name"));
                        user.setProfileImage(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getJSONObject("user").getString("profile_image"));
                        review.setUser(user);
                        review.setCommentsCount(response.getJSONArray("user_reviews").getJSONObject(i).getJSONObject("review").getInt("comments_count"));
                        userReviewList.add(review);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                reviewAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                long endtime = System.currentTimeMillis();
                startTime += endtime - exeTime;
                progressDialog.dismiss();
                System.out.println("details with network " + (endtime - loadTime));
                System.out.println("Total time details() without network: " + startTime);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
            }
        });
    }

    private void initSlider(String img){
        final DefaultSliderView defaultSliderView = new DefaultSliderView(this);
        defaultSliderView
                .image(img)
                .setScaleType(BaseSliderView.ScaleType.Fit)
                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                    @Override
                    public void onSliderClick(BaseSliderView slider) {
                        Toast.makeText(ActivityViewDetail.this, "photo", Toast.LENGTH_SHORT).show();
                    }
                });

        slider.addSlider(defaultSliderView);
        slider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(400000);
        slider.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    // expand arrow animation
    public RotateAnimation expand() {
        RotateAnimation rotate = new RotateAnimation(0, 90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        return rotate;
    }
    //collapse arrow animation
    public RotateAnimation collapse() {
        RotateAnimation rotate = new RotateAnimation(90, 0, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        return rotate;
    }
}
