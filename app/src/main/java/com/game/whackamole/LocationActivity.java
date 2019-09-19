package com.game.whackamole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;

    String name;
    int score;
    int position;
    String location;
    Button backBtn;
    TextView nameTV,scoreTV,placeTV;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Set to fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_location);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        //Get the name from the Entry Activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        location = intent.getStringExtra("location");
        score = intent.getIntExtra("score",0);
        position = intent.getIntExtra("position",1);


        //Check location
        if(location.equals("null") || location.length()<3)location="0,0";

        nameTV=findViewById(R.id.pName);
        nameTV.setText("Name:"+name);
        scoreTV=findViewById(R.id.pScore);
        scoreTV.setText("Score:"+score);
        placeTV=findViewById(R.id.pPlace);
        placeTV.setText("Place:#"+position);

        backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        LatLng pos=null;
        gmap.setMinZoomPreference(12);
        if(location!=null || location.length()>0) {
            String[] loc = location.split(",");

            pos = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
        }
        if(pos!=null) {
            gmap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            gmap.addMarker(new MarkerOptions().position(pos)
                    .title(name));
        }

    }
}
