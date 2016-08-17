package com.example.hasibuzzaman.locationtest;


/* https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=23.7842676,90.369294&radius=500&types=food&key= AIzaSyB3PpqkyKKcYOiEw1XjQ2BsjF6zB_x8peI*/

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
     TextView lattitudeTv;
    TextView longitudeTv,AddressTV;
    Location latlong;
    AddressResultReceiver resultreceiver;
    String Address;
    private AutoCompleteTextView myLocation;
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    private static final int PLACE_PICKER_FLAG = 1;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        lattitudeTv= (TextView) findViewById(R.id.lattitudeTv);
        longitudeTv= (TextView) findViewById(R.id.longitudeTv);
        AddressTV= (TextView) findViewById(R.id.AddressTV);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        resultreceiver = new AddressResultReceiver(new Handler());


        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        /*myLocation = (AutoCompleteTextView) findViewById(R.id.myLocation);

        mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                googleApiClient, BOUNDS_GREATER_SYDNEY, null);

        myLocation.setOnItemClickListener(mAutocompleteClickListener);
*/


    }

    /*private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };*/

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        googleApiClient.disconnect();
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
         LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);






    }

    public void startIntentService()
    {
        Intent intent = new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultreceiver);
        intent.putExtra(Constants.MY_LOCATION,latlong);  // sending the LAt and Long
        Log.e("Start Service ", " Start service");
        startService(intent);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latlong= location;
        startIntentService();
        AddressTV.setText(Address +"");
        lattitudeTv.setText(location.getLatitude()+"");
        longitudeTv.setText(location.getLongitude()+"");


    }

/*    public void send(View view) {
        if (googleApiClient.isConnected() && latlong != null) {
            startIntentService();
        }

    }*/

    public void map(View view) {
        Intent in = new Intent(this, MapsActivity.class);
        in.putExtra(Constants.LATTITUDE,latlong.getLatitude());
        in.putExtra(Constants.LONGITUDE,latlong.getLongitude());
        startActivity(in);
    }


    public class AddressResultReceiver extends ResultReceiver
    {


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Address = resultData.getString(Constants.RESULT_DATA_KEY);
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.e("onReceiveResult","onReceiveResult");
            }


        }
    }



}
