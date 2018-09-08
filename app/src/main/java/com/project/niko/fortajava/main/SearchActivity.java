package com.project.niko.fortajava.main;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.niko.fortajava.Adapter.AdapterLocation;
import com.project.niko.fortajava.Adapter.AdapterRestaurant;
import com.project.niko.fortajava.Helper.HelperAPI;
import com.project.niko.fortajava.Model.City;
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

public class SearchActivity extends AppCompatActivity {

    private EditText et_search;
    private ProgressBar progressBar;

    private RecyclerView rv_res;
    private AdapterRestaurant adapterRestaurant;
    private List<Restaurant> restaurantList = new ArrayList<>();

    private AdapterLocation adapterLocation;
    private List<City> cityList = new ArrayList<>();

    long executionTime = 0, loadTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTime = System.currentTimeMillis();
        setContentView(R.layout.activity_search);
        init();
        rv_res.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapterLocation = new AdapterLocation(cityList, this);
        rv_res.setAdapter(adapterLocation);
        getCityDetail();
    }

    private void init(){
        et_search = this.findViewById(R.id.et_search);
        rv_res = this.findViewById(R.id.rv_res);
        progressBar = this.findViewById(R.id.progressBar);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    adapterRestaurant = new AdapterRestaurant(restaurantList, SearchActivity.this);
                    rv_res.setAdapter(adapterRestaurant);

                    restaurantList.clear();
                    getRestaurantData(0);
                }
                return false;
            }
        });
    }

    private void getRestaurantData(final int start){
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        params.put("q", et_search.getText().toString());
        params.put("start", start);
        params.put("lat", -6.907745);
        params.put("lon", 107.609444);

        HelperAPI.get("search", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long startTime = System.currentTimeMillis();
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

                    executionTime += System.currentTimeMillis() - startTime;
                    if(restaurantList.size() < 100){
                        getRestaurantData(restaurantList.size());
                    } else {
                        adapterRestaurant.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        long elapsedTime = System.currentTimeMillis();
                        System.out.println("Time search() with network: " + (elapsedTime - loadTime));
                        System.out.println("Time search() without network: " + executionTime);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(SearchActivity.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCityDetail(){
        RequestParams params = new RequestParams();
        params.put("query", "jakarta");
        params.put("lat", -6.907745);
        params.put("lon", 107.609444);
        params.put("count", 10);

        HelperAPI.get("locations", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long startTime = System.currentTimeMillis();
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray jsonArray = response.getJSONArray("location_suggestions");
                    for (int i = 0 ; i < jsonArray.length() ; i++){
                        City city = new City();
                        city.setCityId(jsonArray.getJSONObject(i).getString("entity_id"));
                        city.setCityName(jsonArray.getJSONObject(i).getString("title"));
                        city.setEntityType(jsonArray.getJSONObject(i).getString("entity_type"));
                        city.setCountry(jsonArray.getJSONObject(i).getString("country_name"));

                        cityList.add(city);
                    }
                    adapterLocation.notifyDataSetChanged();
                    executionTime += (System.currentTimeMillis() - startTime);
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

    public void getLocation(City city){
        progressBar.setVisibility(View.VISIBLE);
        adapterRestaurant = new AdapterRestaurant(restaurantList, SearchActivity.this);
        rv_res.setAdapter(adapterRestaurant);

        RequestParams params = new RequestParams();
        params.put("entity_id", city.getCityId());
        params.put("entity_type", city.getEntityType());

        HelperAPI.get("location_details", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long startTime = System.currentTimeMillis();
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONArray restorants = response.getJSONArray("best_rated_restaurant");
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

                    adapterRestaurant.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    long endtime = System.currentTimeMillis();
                    executionTime += (endtime - startTime);
                    System.out.println("Time getByCity with network : " + (endtime - loadTime));
                    System.out.println("Time getByCity without network : " + executionTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(SearchActivity.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
}
