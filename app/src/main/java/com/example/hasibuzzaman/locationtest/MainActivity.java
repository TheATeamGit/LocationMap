package com.example.hasibuzzaman.locationtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.ResultReceiver;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
     TextView lattitudeTv;
    TextView longitudeTv,AddressTV;
    Location latlong;
    AddressResultReceiver resultreceiver;
    String me;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        lattitudeTv= (TextView) findViewById(R.id.lattitudeTv);
        longitudeTv= (TextView) findViewById(R.id.longitudeTv);
        AddressTV= (TextView) findViewById(R.id.AddressTV);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        resultreceiver = new AddressResultReceiver(null);

    }



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
        AddressTV.setText(me+"");
        lattitudeTv.setText(location.getLatitude()+"");
        longitudeTv.setText(location.getLongitude()+"");

    }

    public void send(View view) {
        if (googleApiClient.isConnected() && latlong != null) {
            startIntentService();
        }

    }

    public void map(View view) {
        Intent in = new Intent(this, MapsActivity.class);
        in.putExtra(Constants.LATTITUDE,latlong.getLatitude());
        in.putExtra(Constants.LONGITUDE,latlong.getLongitude());
        final double latitude = latlong.getLatitude();
        startActivity(in);
    }


    public class AddressResultReceiver extends ResultReceiver
    {


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            me = resultData.getString(Constants.RESULT_DATA_KEY);
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.e("onReceiveResult","onReceiveResult");
            }


        }
    }



}
