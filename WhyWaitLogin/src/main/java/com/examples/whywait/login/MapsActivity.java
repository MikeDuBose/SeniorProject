package com.examples.whywait.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.Persistence;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressWarnings("serial")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener,
        Serializable{


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    private Marker packsMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    private String objIdIndia = "491342CF-E09A-BD70-FF83-8071C9E5FE00";
    private String objIdSunny = "491342CF-E09A-BD70-FF83-8071C9E5FE00";
    private String objIdLuellas = "925662F7-8DF0-843D-FF7B-1E33C257BD00";
    private String objIdPacks = "D2380829-38DA-E447-FFF3-0465F44FAA00";
    public ArrayList<Backend> foundContacts = new ArrayList<>();
    public ArrayList<Queue> foundQueue = new ArrayList<>();
    static final String userInfo_key = "BackendlessUserInfo";











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
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
        mMap = googleMap;
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }




    public void waitTime(Queue q, Marker marker){

    }






    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationmMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }




    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }




    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId())
        {
            /*case R.id.B_search:
                EditText tf_location =  findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;


                if(!location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if(addressList != null)
                        {
                            for(int i = 0;i<addressList.size();i++)
                            {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                */

            //case R.id.B_restaurants:
                //BELOW IS FOR FINDING RESTAURANTS WITH GOOGLE MAPS
                /*
                mMap.clear();
                String restaurant = "restaurant";
                String url = getUrl(latitude, longitude, restaurant);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                */
                //break;

                /*
            case R.id.saveNew:
                Log.d("The SECONDARYARRAYREF!", "The array at position 0 is " + MapsActivity.this.foundContacts.get(0).getRestaurantName());


                /*final Backend backend = new Backend();
                        backend.setNumGuests( 10 );
                        backend.setRestaurantName( "Packs" );
                        backend.saveAsync( new AsyncCallback<Backend>()
                        {
                        @Override
                        public void handleResponse( Backend savedBackend )
                        {
                        savedBackend = backend;
                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                        Toast.makeText( getBaseContext(), fault.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                        });*/

                //break;


            case R.id.B_to:



                IconGenerator iconGenerator = new IconGenerator(this);
                iconGenerator.setStyle(IconGenerator.STYLE_BLUE);

                Queue queue = new Queue();

                Bitmap b_luella = iconGenerator.makeIcon("Luella's Bar-B-Que\n");
                Bitmap b_packs = iconGenerator.makeIcon("Pack's Tavern\n");
                Bitmap b_india = iconGenerator.makeIcon("India Garden\n");
                Bitmap b_sunny = iconGenerator.makeIcon("Sunny Point Cafe\n");

                LatLng packs = new LatLng(35.595082, -82.549568);
                LatLng luella = new LatLng (35.615265, -82.554634);
                LatLng india = new LatLng(35.579172, -82.521607);
                LatLng sunny = new LatLng(35.578530,-82.588817);

                Marker m_luella = mMap.addMarker(new MarkerOptions()
                        .title("Luellas BBQ")
                        .position(luella)
                        .icon(BitmapDescriptorFactory.fromBitmap(b_luella))
                    );

                Marker m_packs = mMap.addMarker(new MarkerOptions()
                        .title("Packs Tavern")
                        .position(packs)
                        .icon(BitmapDescriptorFactory.fromBitmap(b_packs))
                );

                Marker m_sunny = mMap.addMarker(new MarkerOptions()
                        .title("Sunny Pointe")
                        .position(sunny)
                        .icon(BitmapDescriptorFactory.fromBitmap(b_sunny))
                );

                Marker m_india = mMap.addMarker(new MarkerOptions()
                        .title("India Garden")
                        .position(india)
                        .icon(BitmapDescriptorFactory.fromBitmap(b_india))
                );
                Log.d("addMarkers", "Finished adding markers");

                break;
        }
    }

        @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    //MARKER INFORMATION
    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        Log.d("Marker Title for Click", "" + marker.getTitle());
        Intent intent = new Intent(getApplicationContext(), PopActivity.class);
        Intent intentz = getIntent();
        String message = intentz.getStringExtra(userInfo_key);
        message = message == null ? "" : message;
        Log.d("Message?", "Message here should be some user info I guess?" + message);
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String name = user.getProperty("name").toString();
        Log.d("Name", "Here is JUST The name (we hope?)" + name);


        switch (marker.getTitle()){

            case "Packs Tavern" :

                intent.putExtra("RestaurantName", marker.getTitle());
                intent.putExtra("name", name);
                startActivity(intent);
                return true;

            case "Luellas BBQ" : Log.d("Click", "Luella's");
                intent.putExtra("RestaurantName", marker.getTitle());
                intent.putExtra("name", name);
                startActivity(intent);
                return true;


            case "India Garden" : Log.d("Click", "India Garden");
                intent.putExtra("RestaurantName", marker.getTitle());
                intent.putExtra("name", name);
                startActivity(intent);
                return true;

            case "Sunny Pointe" : Log.d("Click", "Sunny Point");
                intent.putExtra("RestaurantName", marker.getTitle());
                intent.putExtra("name", name);
                startActivity(intent);

                return true;
        }
        return false;
    }










}