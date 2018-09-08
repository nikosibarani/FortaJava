package com.project.niko.fortajava.main;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.niko.fortajava.Adapter.AdapterRestaurant;
import com.project.niko.fortajava.Helper.HelperAPI;
import com.project.niko.fortajava.Model.Location;
import com.project.niko.fortajava.Model.Restaurant;
import com.project.niko.fortajava.Model.UserRating;
import com.project.niko.fortajava.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ActivityViewResult extends AppCompatActivity {

    private RecyclerView rv_result_rest;
    private TextView tv_result_count;
    private ProgressBar progressBar;
    private AdapterRestaurant adapterRestaurant;
    private List<Restaurant> restaurantList = new ArrayList<>();

    long timeWithoutNetwork = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initial();

        adapterRestaurant = new AdapterRestaurant(restaurantList,this);
        rv_result_rest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_result_rest.setAdapter(adapterRestaurant);

        getRestaurantData(0);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getRestaurantData(0);
//            }
//        }).start();
    }

    private void getRestaurantData(final int start) {
        RequestParams params = new RequestParams();
        params.put("lat", -6.907745);
        params.put("lon", 107.609444);
        params.put("start", start);
        params.put("category", "1");

        progressBar.setVisibility(View.VISIBLE);

        HelperAPI.get("search", params, new JsonHttpResponseHandler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long startTime = System.currentTimeMillis();
                System.out.println(response.toString());
                System.out.println("tasijfsdf " + Thread.currentThread().getName());
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray restorants = response.getJSONArray("restaurants");
                    for (int i = 0 ; i < restorants.length() ; i++){
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(restorants.getJSONObject(i).getJSONObject("restaurant").getString("id"));
                        restaurant.setName(restorants.getJSONObject(i).getJSONObject("restaurant").getString("name"));
                        restaurant.setUrl(restorants.getJSONObject(i).getJSONObject("restaurant").getString("url"));

                        Location location = new Location();
                        location.setAddress(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("address"));
                        location.setLocality(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality"));
                        location.setCity(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("city"));
                        location.setCityId(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getInt("city_id"));
                        location.setLatitude(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("latitude"));
                        location.setLongitude(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("longitude"));
                        location.setZipcode(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("zipcode"));
                        location.setCountryId(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getInt("country_id"));
                        location.setLocalityVerbose(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality_verbose"));
                        restaurant.setLocation(location);
                        restaurant.setCuisines(restorants.getJSONObject(i).getJSONObject("restaurant").getString("cuisines"));
                        restaurant.setAverageCostForTwo(restorants.getJSONObject(i).getJSONObject("restaurant").getInt("average_cost_for_two"));

                        UserRating userRating = new UserRating();
                        userRating.setAggregateRating(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating"));
                        userRating.setRatingText(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_text"));
                        userRating.setRatingColor(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("rating_color"));
                        userRating.setVotes(restorants.getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("votes"));
                        restaurant.setUserRating(userRating);

                        restaurant.setPhotosUrl(restorants.getJSONObject(i).getJSONObject("restaurant").getString("photos_url"));
                        restaurant.setFeaturedImage(restorants.getJSONObject(i).getJSONObject("restaurant").getString("featured_image"));
                        restaurantList.add(restaurant);
                    }

                    timeWithoutNetwork += System.currentTimeMillis() - startTime;
                    if(restaurantList.size() < 100){
                        if(restaurantList.size() > 80){
                            getRestaurantData(0);
                        } else {
                            getRestaurantData(restaurantList.size());
                        }
                    } else {
                        tv_result_count.setText(restaurantList.size() + " Hasil");
                        adapterRestaurant.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        long endTime = System.currentTimeMillis();
                        System.out.println("Time get" + getIntent().getStringExtra("key") + "() " + (endTime - getIntent().getLongExtra("time", System.currentTimeMillis())));
                        System.out.println("Time result without network: " + timeWithoutNetwork);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(ActivityViewResult.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }
        });
    }

    private void initial(){
        rv_result_rest = this.findViewById(R.id.rv_search_result);
        tv_result_count = this.findViewById(R.id.tv_result_count);
        progressBar = this.findViewById(R.id.progressBar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
