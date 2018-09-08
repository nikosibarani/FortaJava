package com.project.niko.fortajava.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class FragmentHome extends Fragment {
    private Context context;
    private AdapterRestaurant adapterRestaurant;
    private Location myLocation = null;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private TextView tv_count = null;
    private TextView tv_location_name = null;
    private RecyclerView rv_list_restorant = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();
        initial(view);

        adapterRestaurant = new AdapterRestaurant(restaurantList, context);
        rv_list_restorant.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_list_restorant.setAdapter(adapterRestaurant);

        getRestaurantData();

        return view;
    }

    private void initial(View view){
        tv_location_name = view.findViewById(R.id.tv_location_name);
        rv_list_restorant = view.findViewById(R.id.rv_nearby_rest);
        tv_count = view.findViewById(R.id.tv_count);


        view.findViewById(R.id.cv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
                Intent intent = new Intent(context, ActivityViewResult.class);
//                Intent intent = new Intent(context, ActivityViewResult.class);
//                intent.putExtra("title", "Delivery");
//                intent.putExtra("key", "category");
//                intent.putExtra("time", System.currentTimeMillis());
//                startActivity(intent);
            }
        });
    }

    private void getRestaurantData(){
        RequestParams params = new RequestParams();
        params.put("lat", -6.907745);
        params.put("lon", 107.609444);

        final ProgressDialog progressDialog = ProgressDialog.show(context, null, "Loading");

        HelperAPI.get("geocode", params, new JsonHttpResponseHandler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                long timeWithoutNetwork = System.currentTimeMillis();
                super.onSuccess(statusCode, headers, response);
                myLocation = new Location();
                try {
                    myLocation.setAddress(response.getJSONObject("location").getString("title") + ", "
                            + response.getJSONObject("location").getString("city_name"));
                    myLocation.setLatitude(response.getJSONObject("location").getDouble("latitude"));
                    myLocation.setLongitude(response.getJSONObject("location").getDouble("longitude"));
                    tv_location_name.setText(myLocation.getAddress());

                    JSONArray restorants = response.getJSONArray("nearby_restaurants");
                    tv_count.setText(restorants.length() + " Restaurant");

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
                    progressDialog.dismiss();
                    long currentTime = System.currentTimeMillis();
                    System.out.println("Total time home + network: " + (currentTime - ((MainActivity)context).startTime));
                    System.out.println("Total time without network: " + (currentTime - timeWithoutNetwork));
                    System.out.println("total time : " + less(currentTime, timeWithoutNetwork));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Long less(Long x, Long y){
        return x - y;
    }
}
