package com.postpc.mygiftcrads;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.Collator;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.postpc.mygiftcrads.MyClusterManagerRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float  DEFAULT_ZOOM = 13f;
    private Marker curent_marker = null;


    //widgets
    private AutoCompleteTextView mSearchtext;
    private ImageView mGps;

    //VARS
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private Collator FirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: onCreate in mapActivity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchtext = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        getLocationPermission();
        Places.initialize(getApplicationContext(), "AIzaSyDa_GZtP3QXkukENe7bUSDJI1GYR5GnFPU");
        PlacesClient placesClient = Places.createClient(this);
        Log.d("*******", "before");
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    private void addMapMarker(LatLng latlng, String title, String snippet, int logo){
        if(mMap != null){
            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(this.getApplicationContext(), mMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            // Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString());
            try{
                ClusterMarker newClusterMarker = new ClusterMarker(
                        latlng,
                        //userLocation.getUser().getUsername(),
                        snippet, title,
                        logo
                        //userLocation.getUser()
                );
                mClusterManager.addItem(newClusterMarker);
                mClusterMarkers.add(newClusterMarker);

            }catch (NullPointerException e){
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
            }

            mClusterManager.cluster();
//            moveCamera(latlng, 15, "kaka");
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));
        }

    }
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLovation: getting the device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            //Log.d(TAG, "onComplete: currrent location long,lat:" + currentLocation.getLongitude() + currentLocation.getLatitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        }
                        else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException:" + e.getMessage());
        }
    }


    private void moveCamera(LatLng latlng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving camera to lat" + latlng.latitude + "lng" + latlng.longitude);
        if(curent_marker != null)
        {
            curent_marker.remove();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));
        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);
            curent_marker = mMap.addMarker(options);// adding the marker on the map

        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init(){
        Log.d(TAG, "init: initializing");
//        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.input_search);
//        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);


//        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(new LatLng()));
        mSearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()== KeyEvent.ACTION_DOWN
                        || keyEvent.getAction()== KeyEvent.KEYCODE_ENTER) {
                    // execute our method for searching
                    geolocate();
                }
                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        HideSoftKeyboard();
        addStoresMarkers();
    }

    private void addStoresMarkers()
    {
        Intent intent = getIntent();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String stores = intent.getStringExtra("stores");
        String sums = intent.getStringExtra("sums");
        String[] storesArray = stores.split(";");
        final String[] sumsArray = sums.split(";");
        if(!storesArray[0].equals(""))
        {
            int i = 0;
            for (final String curStore : storesArray)
            {
                final int finalI = i;
                db.collection("locations").document(curStore).
                        get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists())
                        {
                            Map<String, Object> map = document.getData();
                            if(map != null)
                            {
                                for(Map.Entry<String, Object> entry : map.entrySet())
                                {
                                    int logo = -1;
                                    if(curStore.equals("ace"))
                                    {
                                        logo = R.mipmap.ace_marker_logo;
                                    }
                                    else if(curStore.equals("fox"))
                                    {
                                        logo = R.drawable.fox_marker_logo;
                                    }
                                    else if(curStore.equals("rami levy"))
                                    {
                                        logo = R.mipmap.rami_map_logo;
                                    }
                                    else if(curStore.equals("bug"))
                                    {
                                        logo = R.mipmap.bug_map_logo;
                                    }
                                    GeoPoint curgeo = (GeoPoint) entry.getValue();
                                    addMapMarker(new LatLng(curgeo.getLatitude(), curgeo.getLongitude())
                                            , curStore.toUpperCase(),sumsArray[finalI] + " NIS", logo);
                                }
                            }
                        }
                    }
                });
                i ++;
            }
        }
    }

    private void geolocate(){
        Log.d(TAG, "geolocate: geolocating");
        String searchString = mSearchtext.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.e(TAG, "geolocate: IOexception" + e.getMessage());
        }
        if(list.size() > 0 ){
            Address address = list.get(0);
            Log.d(TAG, "geolocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
        Log.d(TAG, "getLocationPermission: finished the getLocationPermission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed ");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialized out map
                    initMap();
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("*******", "google map" + googleMap);
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted){
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  // make a blue dot on my location!
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void HideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}