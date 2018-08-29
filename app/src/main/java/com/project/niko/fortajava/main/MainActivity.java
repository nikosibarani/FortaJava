package com.project.niko.fortajava.main;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.niko.fortajava.Adapter.AdapterCategory;
import com.project.niko.fortajava.Helper.HelperAPI;
import com.project.niko.fortajava.Model.Category;
import com.project.niko.fortajava.Model.Location;
import com.project.niko.fortajava.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.project.niko.fortajava.R.id.container;

public class MainActivity extends AppCompatActivity{

    private Location location;

    private RecyclerView rv_nav_category;
    private ProgressBar progressBar;
    private TextView tv_location_name;

    private List<Category> categoryList = new ArrayList<>();

    private AdapterCategory adapterCategory;

    private Menu menu;
    public long executionTime = 0, startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                startTime = System.currentTimeMillis();
                categoryList.clear();
                getCategoryData();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initial();

        adapterCategory = new AdapterCategory(categoryList, this);
        rv_nav_category.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_nav_category.setAdapter(adapterCategory);

        //start fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(container, new FragmentHome()).commit();
    }

    private void initial(){
        rv_nav_category = this.findViewById(R.id.rv_category_nav);
        progressBar = this.findViewById(R.id.progressBar);
    }

    private void getCategoryData(){
        final long startTime = System.currentTimeMillis();
        progressBar.setVisibility(View.VISIBLE);
        HelperAPI.get("categories", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray arrayCategory;
                try {
                    arrayCategory = response.getJSONArray("categories");
                    for (int i = 0 ; i < arrayCategory.length() ; i++){
                        Category category = new Category(arrayCategory.getJSONObject(i).getJSONObject("categories").getString("id"),
                                arrayCategory.getJSONObject(i).getJSONObject("categories").getString("name"));
                        categoryList.add(category);
                    }
                    adapterCategory.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    executionTime += System.currentTimeMillis() - startTime;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainActivity.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_container; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //notification
        final MenuItem refresh = menu.findItem(R.id.refresh);
        FrameLayout rootView = (FrameLayout) refresh.getActionView();

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(refresh);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                //finish();
                Intent i = getApplicationContext().getPackageManager()
                        .getLaunchIntentForPackage(this.getPackageName());
                assert i != null;
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
